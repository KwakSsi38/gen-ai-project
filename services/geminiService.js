import {GoogleGenAI, createUserContent, createPartFromUri} from "@google/genai";
import path from "path";

const ai = new GoogleGenAI({});

const getMimeType = (filePath) => {
  const ext = path.extname(filePath).toLowerCase();
  switch (ext) {
    case ".png":
      return "image/png";
    case ".jpg":
    case ".jpeg":
      return "image/jpeg";
    default:
      throw new Error("지원하지 않는 이미지 형식입니다: " + ext);
  }
};

export const sendToGemini = async (imagePath) => {
  const mimeType = getMimeType(imagePath);

  const ai = new GoogleGenAI({apiKey: process.env.GEMINI_API_KEY});
  const myfile = await ai.files.upload({
    file: imagePath,
    config: {mimeType: mimeType},
  });

  const prompts =
    "시스템 UI나 화면 텍스트 등 인터페이스 요소는 무시하고, 이미지 안의 시각적 구성만 설명해. 위치, 색상, 배치를 고려해 구체적이고 균형 잡힌 묘사를 100자 내외의 한글 문장으로 제공해. 불필요한 서두 없이 설명 본문부터 바로 시작해.";

  const response = await ai.models.generateContent({
    model: "gemini-2.0-flash",
    contents: createUserContent([createPartFromUri(myfile.uri, myfile.mimeType), prompts]),
  });

  return response.text;
};
