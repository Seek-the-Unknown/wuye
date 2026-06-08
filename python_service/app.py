"""
车牌识别服务 - HyperLPR3 + FastAPI
提供给 Java 后端调用的 REST API
"""
import os
import shutil
import tempfile
import logging
from contextlib import asynccontextmanager

import cv2
import numpy as np
from fastapi import FastAPI, File, UploadFile, HTTPException
import hyperlpr3 as lpr3

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
logger = logging.getLogger(__name__)

MIN_CONFIDENCE = float(os.getenv("LPR_MIN_CONFIDENCE", "0.7"))
MODEL_DIR = os.getenv("LPR_MODEL_DIR", os.path.join(os.path.dirname(__file__), "model"))

catcher = None


@asynccontextmanager
async def lifespan(app: FastAPI):
    global catcher
    logger.info(f"Loading HyperLPR3 model from {MODEL_DIR}...")
    kwargs = {}
    if MODEL_DIR and os.path.exists(MODEL_DIR):
        kwargs["folder"] = MODEL_DIR
    catcher = lpr3.LicensePlateCatcher(**kwargs)
    logger.info("HyperLPR3 model loaded successfully")
    yield
    catcher = None


app = FastAPI(title="License Plate Recognition Service", lifespan=lifespan)


@app.get("/health")
async def health():
    return {"status": "ok", "model_loaded": catcher is not None}


@app.post("/recognize")
async def recognize(file: UploadFile = File(...)):
    if catcher is None:
        raise HTTPException(status_code=503, detail="Model not loaded yet")

    suffix = os.path.splitext(file.filename or "image.jpg")[1] or ".jpg"
    try:
        contents = await file.read()
        nparr = np.frombuffer(contents, np.uint8)
        img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
        
        logger.info(f"Recognizing image, size={len(contents)}")
        if img is None:
            return {"success": False, "error": "无法读取图片文件"}
            
        results = catcher(img)

        if not results or len(results) == 0:
            return {"success": False, "error": "未检测到车牌"}

        # results format: [[plate_text, confidence, class_idx, bbox], ...]
        best = max(results, key=lambda r: float(r[1]))
        plate = str(best[0])
        confidence = float(best[1])

        if confidence < MIN_CONFIDENCE:
            return {
                "success": False,
                "plate_number": plate,
                "confidence": round(confidence, 4),
                "error": f"识别置信度过低 ({confidence:.1%})，请重新拍照"
            }

        return {"success": True, "plate_number": plate, "confidence": round(confidence, 4)}

    except Exception as e:
        logger.exception("Recognition failed")
        return {"success": False, "error": str(e)}


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
