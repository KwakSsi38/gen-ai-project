// routes/upload.js

import {Router} from "express";
import multer, {diskStorage} from "multer";
import {extname} from "path";
import {existsSync, mkdirSync} from "fs";
import {sendToGemini} from "../services/geminiService.js";
import {deleteImage} from "../services/imageHandler.js";

const router = Router();

const storage = diskStorage({
  destination: function (req, file, cb) {
    const uploadPath = "uploads/";
    if (!existsSync(uploadPath)) {
      mkdirSync(uploadPath);
    }
    cb(null, uploadPath);
  },
  filename: function (req, file, cb) {
    const ext = extname(file.originalname);
    const uniqueName = `${Date.now()}-${Math.round(Math.random() * 1e9)}${ext}`;
    cb(null, uniqueName);
  },
});
const upload = multer({storage});

router.post("/upload", upload.single("image"), async (req, res) => {
  if (!req.file) {
    return res.status(400).json({error: "이미지가 첨부되지 않았습니다."});
  }

  const imagePath = req.file.path;
  console.log("imagePath: ", imagePath);

  try {
    const caption = await sendToGemini(imagePath);
    await deleteImage(imagePath);

    return res.json({caption});
  } catch (err) {
    console.error("처리 중 오류:", err);
    await deleteImage(imagePath);
    return res.status(500).json({error: "이미지 처리 실패"});
  }
});

export default router;
