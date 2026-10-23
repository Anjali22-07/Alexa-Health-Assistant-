import { useEffect, useState } from "react";
import "./App.css";

const SpeechRecognition =
  window.SpeechRecognition || window.webkitSpeechRecognition;

export default function App() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [records, setRecords] = useState([]);
  const [busy, setBusy] = useState(false);
  const [listening, setListening] = useState(false);

  async function loadRecords() {
    try {
      const r = await fetch("/api/V1/healthRecords");
      if (r.ok) setRecords(await r.json());
    } catch {
      /* ignore for now */
    }
  }

 const lastNudge = useRef(null);

useEffect(() => {
  async function poll() {
    try {
      const r = await fetch("/api/V1/chat/proactive");
      if (r.status !== 200) return;
      const n = await r.json();
      if (n.createdAt !== lastNudge.current) {
        lastNudge.current = n.createdAt;
        setMessages((m) => [...m, { role: "alexa", text: "(Check-in) " + n.text }]);
        speak(n.text);
        loadRecords();
      }
    } catch {
      /* ignore */
    }
  }
  poll();
  const id = setInterval(poll, 20000);
  return () => clearInterval(id);
}, []);
   
  useEffect(() => {
    loadRecords();
  }, []);

  function speak(text) {
    if ("speechSynthesis" in window && text) {
      speechSynthesis.speak(new SpeechSynthesisUtterance(text));
    }
  }

  async function send(text) {
    const message = text.trim();
    if (!message || busy) return;
    setMessages((m) => [...m, { role: "you", text: message }]);
    setInput("");
    setBusy(true);
    try {
      const r = await fetch("/api/V1/chat", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ message }),
      });
      const data = await r.json();
      const reply = data.reply || data.error || "No reply from the server.";
      setMessages((m) => [...m, { role: "alexa", text: reply }]);
      speak(reply);
    } catch {
      setMessages((m) => [
        ...m,
        { role: "alexa", text: "I could not reach the server." },
      ]);
    } finally {
      setBusy(false);
      loadRecords();
    }
  }

  function listen() {
    if (!SpeechRecognition) {
      alert("Voice input needs Chrome.");
      return;
    }
    const rec = new SpeechRecognition();
    rec.lang = "en-IN";
    rec.onstart = () => setListening(true);
    rec.onend = () => setListening(false);
    rec.onresult = (e) => send(e.results[0][0].transcript);
    rec.start();
  }

  const sorted = [...records].sort(
    (a, b) => new Date(b.recordedAt) - new Date(a.recordedAt)
  );

  return (
    <div className="app">
      <section className="panel">
        <h2>Alexa+ Simulator</h2>
        <p className="hint">
          A simulated Alexa+ host. Speak or type, and the assistant saves or
          fetches health records through the MCP server.
        </p>
        <div className="log">
          {messages.map((m, i) => (
            <div key={i} className={m.role}>
              <strong>{m.role === "you" ? "You" : "Alexa+"}:</strong> {m.text}
            </div>
          ))}
          {busy && <div className="alexa">Thinking...</div>}
        </div>
        <div className="row">
          <input
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && send(input)}
            placeholder="Type, or press the mic and speak"
          />
          <button onClick={listen} disabled={busy}>
            {listening ? "Listening..." : "🎤"}
          </button>
          <button onClick={() => send(input)} disabled={busy}>
            Send
          </button>
          {/* <button onClick={checkIn}>Daily check-in</button> */}
        </div>
      </section>

      <section className="panel">
         {/* {nudges && (
      <pre style={{ whiteSpace: "pre-wrap", fontFamily: "inherit", background: "#fff8e1", padding: 12, borderRadius: 8 }}>
        {nudges}
      </pre>
    )} */}
        <h2>Health timeline</h2>
        {sorted.length === 0 && <p className="hint">No records yet.</p>}
        <ul className="timeline">
          {sorted.map((r, i) => (
            <li key={r.id || i}>
              <div className="when">
                {new Date(r.recordedAt).toLocaleString("en-IN")}
              </div>
              <div>
                <span className="badge">{r.recordType}</span>{" "}
                {r.content}
                {r.source && (
                  <span className={"src " + r.source}> {r.source}</span>
                )}
              </div>
            </li>
          ))}
        </ul>
           
      </section>
    </div>
  );
}