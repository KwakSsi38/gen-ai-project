// 위치: app.js
import express from "express";
import dotenv from "dotenv";
import cors from "cors";
import uploadRouter from "./routes/upload.js";
import {writeFileSync, existsSync, mkdirSync} from "fs";
import {dirname} from "path";

// 🌱 1. .env 파일 로드
dotenv.config();

// 🔐 2. 서비스 계정 키 복원
const credentialPath = "./auth/gemini-sa.json";
const dir = dirname(credentialPath);

// 디렉토리가 없으면 생성
if (!existsSync(dir)) {
  mkdirSync(dir, {recursive: true});
}

// base64 → JSON 복원 후 파일로 저장
const credentialJson = Buffer.from(process.env.GOOGLE_CREDENTIALS_B64, "base64").toString("utf-8");

writeFileSync(credentialPath, credentialJson);

// GCP SDK가 참조할 인증 환경변수 설정
process.env.GOOGLE_APPLICATION_CREDENTIALS = credentialPath;

// 🚀 3. 서버 설정
const app = express();
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({extended: true}));

// 📦 4. 라우터 등록
app.use("/api", uploadRouter);

// 🌐 5. 서버 실행
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`✅ 서버 실행 중: http://localhost:${PORT}`);
});
