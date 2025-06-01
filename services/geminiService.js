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

  const prompts = [
    "이 이미지를 보지 못하는 사람에게 구체적이면서 100자 내외로 묘사해줘.",
    "위치, 색상, 배치를 고려해 상세히 설명해줘.",
    "이 이미지의 내용을 한 문장으로 요약해줘.",
    "이 이미지를 시각장애인이 이해할 수 있도록 핵심 요소만 간결하게 설명해줘.",
    "시각장애인을 위해 이 이미지를 두 문장 이내로 설명해줘.",
    "이미지를 직접 볼 수 없는 사람에게 핵심이 잘 드러나도록 설명해줘.",
    "배경과 중심 대상이 무엇인지 구분해서 두 문장 내외로 알려줘.",
    "이 장면에서 가장 중요한 내용이 무엇인지 한 문장으로 요약해줘.",
  ];

  const results = [];

  for (let prompt of prompts) {
    prompt = prompt + "'물론입니다' 같은 서두 없이 핵심 설명부터 바로 시작해. 한글로 대답해줘.";
    const response = await ai.models.generateContent({
      model: "gemini-2.0-flash",
      contents: createUserContent([createPartFromUri(myfile.uri, myfile.mimeType), prompt]),
    });
    results.push({
      prompt,
      response: response.text,
    });
  }
  return results;
};
