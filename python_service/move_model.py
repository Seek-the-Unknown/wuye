import shutil
import os

src = r"E:\Users\刘紫硕\.hyperlpr3"
dst = r"E:\python_service\model"

if os.path.exists(src) and not os.path.exists(dst):
    shutil.copytree(src, dst)
    print("Copied successfully.")
elif os.path.exists(dst):
    print("Destination already exists.")
else:
    print("Source not found.")
