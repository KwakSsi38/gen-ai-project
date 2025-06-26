// 위치: services/imageHandler.js

import { unlink } from "fs/promises";

export const deleteImage = async (imagePath) => {
  try {
    await unlink(imagePath);
    console.log(`이미지 삭제 완료: ${imagePath}`);
  } catch (err) {
    console.warn(`이미지 삭제 실패: ${imagePath}`, err.message);
  }
};
