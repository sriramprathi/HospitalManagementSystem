import { login } from "./services/ApiService";

export default function App() {
  const test = async () => {
    try {
      const data = await login("wrong.user@1234", "wrong");
      console.log(data);
    } catch (e) {
      console.log("Error:", e.message);
    }
  };
  return <button onClick={test}>Test API</button>;
}