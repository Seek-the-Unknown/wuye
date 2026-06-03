<template>
  <div class="google-auth-container">
    <!-- Stunning Ambient Shifting Nebula Spheres -->
    <div class="glow-sphere sphere-1"></div>
    <div class="glow-sphere sphere-2"></div>
    <div class="glow-sphere sphere-3"></div>
    <div class="glow-sphere sphere-4"></div>

    <!-- Twinkling Floating Cosmic Particles -->
    <div class="particle-container">
      <div class="particle" style="left: 8%; animation-duration: 9s; animation-delay: 0s;"></div>
      <div class="particle" style="left: 18%; animation-duration: 13s; animation-delay: 1.2s;"></div>
      <div class="particle" style="left: 32%; animation-duration: 11s; animation-delay: 0.5s;"></div>
      <div class="particle" style="left: 48%; animation-duration: 15s; animation-delay: 2s;"></div>
      <div class="particle" style="left: 62%; animation-duration: 10s; animation-delay: 0.8s;"></div>
      <div class="particle" style="left: 78%; animation-duration: 14s; animation-delay: 2.5s;"></div>
      <div class="particle" style="left: 88%; animation-duration: 12s; animation-delay: 0.3s;"></div>
      <div class="particle" style="left: 14%; animation-duration: 16s; animation-delay: 3.5s;"></div>
      <div class="particle" style="left: 44%; animation-duration: 11s; animation-delay: 3s;"></div>
      <div class="particle" style="left: 74%; animation-duration: 13s; animation-delay: 1.8s;"></div>
    </div>

    <div class="google-auth-box">
      <!-- Left Pane: Branding & Welcome (Gemini Premium design) -->
      <div class="brand-section">
        <div class="brand-logo-container">
          <!-- Elegant abstract sparkle logo using inline SVG with twinkling IDs -->
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="brand-logo-svg">
            <defs>
              <linearGradient id="geminiSparkleGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" stop-color="#7a22ff" />
                <stop offset="50%" stop-color="#ff007a" />
                <stop offset="100%" stop-color="#00e5ff" />
              </linearGradient>
            </defs>
            <path id="sparkle-main" fill="url(#geminiSparkleGrad)" d="M12 2c.4 3.7 3.3 6.6 7 7-3.7.4-6.6 3.3-7 7-.4-3.7-3.3-6.6-7-7 3.7-.4 6.6-3.3 7-7z"/>
            <path id="sparkle-sub1" fill="url(#geminiSparkleGrad)" opacity="0.8" d="M19 14c.2 1.8 1.6 3.2 3.5 3.4-1.8.2-3.2 1.6-3.4 3.5-.2-1.8-1.6-3.2-3.5-3.4 1.8-.2 3.2-1.6 3.4-3.5z"/>
            <path id="sparkle-sub2" fill="url(#geminiSparkleGrad)" opacity="0.6" d="M5 4c.1 1 .9 1.8 2 1.9-1 .1-1.8.9-1.9 2C5 7 4.2 6.2 3.1 6.1 4.2 6 5 5.2 5 4z"/>
          </svg>
          <span class="system-badge">Property System</span>
        </div>
        <h1 class="brand-title">{{ title }}</h1>
        <p class="brand-desc">
          创建您的物管系统账号，开启智能、高效、全天候的在线社区生活与物业协同管理体验。
        </p>
        <div class="brand-features">
          <div class="feature-item">
            <svg class="feature-svg-icon" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
            </svg>
            <span>两步快速完成账号注册与绑定</span>
          </div>
          <div class="feature-item">
            <svg class="feature-svg-icon" viewBox="0 0 24 24" fill="currentColor">
              <path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z"/>
            </svg>
            <span>强密码哈希存储，极佳安全性</span>
          </div>
          <div class="feature-item">
            <svg class="feature-svg-icon" viewBox="0 0 24 24" fill="currentColor">
              <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
            </svg>
            <span>极简交互，更佳页面流畅度</span>
          </div>
        </div>
      </div>

      <!-- Divider Line -->
      <div class="auth-divider"></div>

      <!-- Right Pane: Form Card -->
      <div class="form-section">
        <!-- Mobile Header (Visible only on mobile/tablet screens) -->
        <div class="mobile-header">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="brand-logo-svg">
            <path id="sparkle-main-mobile" fill="url(#geminiSparkleGrad)" d="M12 2c.4 3.7 3.3 6.6 7 7-3.7.4-6.6 3.3-7 7-.4-3.7-3.3-6.6-7-7 3.7-.4 6.6-3.3 7-7z"/>
            <path id="sparkle-sub1-mobile" fill="url(#geminiSparkleGrad)" opacity="0.8" d="M19 14c.2 1.8 1.6 3.2 3.5 3.4-1.8.2-3.2 1.6-3.4 3.5-.2-1.8-1.6-3.2-3.5-3.4 1.8-.2 3.2-1.6 3.4-3.5z"/>
            <path id="sparkle-sub2-mobile" fill="url(#geminiSparkleGrad)" opacity="0.6" d="M5 4c.1 1 .9 1.8 2 1.9-1 .1-1.8.9-1.9 2C5 7 4.2 6.2 3.1 6.1 4.2 6 5 5.2 5 4z"/>
          </svg>
          <h2 class="mobile-title">{{ title }}</h2>
          <p class="mobile-desc">创建您的账号</p>
        </div>

        <el-form ref="registerRef" :model="registerForm" :rules="registerRules" class="register-form">
          <el-form-item prop="username" class="google-form-item">
            <div class="google-input-wrapper" :class="{ 'is-focused': focusField === 'username', 'has-value': registerForm.username }">
              <span class="input-label">账号</span>
              <el-input
                v-model="registerForm.username"
                type="text"
                size="large"
                auto-complete="off"
                placeholder="设置登录账号 (2-20位)"
                @focus="focusField = 'username'"
                @blur="focusField = ''"
              />
            </div>
          </el-form-item>

          <el-form-item prop="password" class="google-form-item">
            <div class="google-input-wrapper" :class="{ 'is-focused': focusField === 'password', 'has-value': registerForm.password }">
              <span class="input-label">密码</span>
              <el-input
                v-model="registerForm.password"
                type="password"
                size="large"
                auto-complete="off"
                placeholder="设置密码 (5-20位)"
                show-password
                @focus="focusField = 'password'"
                @blur="focusField = ''"
              />
            </div>
          </el-form-item>

          <el-form-item prop="confirmPassword" class="google-form-item">
            <div class="google-input-wrapper" :class="{ 'is-focused': focusField === 'confirmPassword', 'has-value': registerForm.confirmPassword }">
              <span class="input-label">确认密码</span>
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                size="large"
                auto-complete="off"
                placeholder="请再次输入您的密码"
                show-password
                @focus="focusField = 'confirmPassword'"
                @blur="focusField = ''"
                @keyup.enter="handleRegister"
              />
            </div>
          </el-form-item>

          <el-form-item prop="code" v-if="captchaEnabled" class="google-form-item captcha-item">
            <div class="google-input-wrapper" style="flex: 1;" :class="{ 'is-focused': focusField === 'code', 'has-value': registerForm.code }">
              <span class="input-label">验证码</span>
              <el-input
                v-model="registerForm.code"
                size="large"
                auto-complete="off"
                placeholder="验证码"
                @focus="focusField = 'code'"
                @blur="focusField = ''"
                @keyup.enter="handleRegister"
              />
            </div>
            <div class="register-code">
              <img :src="codeUrl" @click="getCode" class="register-code-img" title="点击刷新验证码"/>
            </div>
          </el-form-item>

          <el-form-item class="action-item" style="margin-top: 24px;">
            <el-button
              :loading="loading"
              type="primary"
              class="google-btn"
              @click.prevent="handleRegister"
            >
              <span v-if="!loading">立即注册</span>
              <span v-else>注册中...</span>
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <span>已有账号？</span>
            <router-link class="register-link" :to="'/login'">直接登录</router-link>
          </div>
        </el-form>
      </div>
    </div>

    <!-- Footer -->
    <div class="google-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script setup>
import { ElMessageBox } from "element-plus"
import { getCodeImg, register } from "@/api/login"
import defaultSettings from '@/settings'

const title = import.meta.env.VITE_APP_TITLE
const footerContent = defaultSettings.footerContent
const router = useRouter()
const { proxy } = getCurrentInstance()

const focusField = ref("")

const registerForm = ref({
  username: "",
  password: "",
  confirmPassword: "",
  code: "",
  uuid: ""
})

const equalToPassword = (rule, value, callback) => {
  if (registerForm.value.password !== value) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, trigger: "blur", message: "请输入您的账号" },
    { min: 2, max: 20, message: "用户账号长度必须介于 2 和 20 之间", trigger: "blur" }
  ],
  password: [
    { required: true, trigger: "blur", message: "请输入您的密码" },
    { min: 5, max: 20, message: "用户密码长度必须介于 5 和 20 之间", trigger: "blur" },
    { pattern: /^[^<>"'|\\]+$/, message: "不能包含非法字符：< > \" ' \\\ |", trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, trigger: "blur", message: "请再次输入您的密码" },
    { required: true, validator: equalToPassword, trigger: "blur" }
  ],
  code: [{ required: true, trigger: "change", message: "请输入验证码" }]
}

const codeUrl = ref("")
const loading = ref(false)
const captchaEnabled = ref(true)

function handleRegister() {
  proxy.$refs.registerRef.validate(valid => {
    if (valid) {
      loading.value = true
      register(registerForm.value).then(res => {
        const username = registerForm.value.username
        ElMessageBox.alert("<font color='#00e5ff'>恭喜你，您的账号 <b>" + username + "</b> 注册成功！</font>", "系统提示", {
          dangerouslyUseHTMLString: true,
          type: "success",
          confirmButtonText: "去登录",
          customClass: "google-message-box"
        }).then(() => {
          router.push("/login")
        }).catch(() => {})
      }).catch(() => {
        loading.value = false
        if (captchaEnabled.value) {
          getCode()
        }
      })
    }
  })
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img
      registerForm.value.uuid = res.uuid
    }
  })
}

getCode()
</script>

<style lang="scss" scoped>
@use "@/assets/styles/auth.scss";
</style>
