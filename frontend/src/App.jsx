import { useEffect, useRef, useState } from "react";
import "./App.css";

const SpeechRecognition =
  window.SpeechRecognition || window.webkitSpeechRecognition;

  const TZ = "Asia/Kolkata";

const isGarmin = (r) => r.source === "GARMIN";
const byTimeDesc = (a, b) => new Date(b.recordedAt) - new Date(a.recordedAt);

function icon(r) {
  const t = (r.recordType || "").toUpperCase();
  if (t.includes("SLEEP")) return "😴";
  if (t.includes("HEART")) return "❤️";
  if (t.includes("STEP")) return "👟";
  if (t.includes("STRESS")) return "🧘";
  if (t.includes("SYMPTOM")) return "🤒";
  if (t.includes("MEDIC")) return "💊";
  if (t.includes("SUPPLEMENT") || t.includes("VITAMIN")) return "🌿";
  return isGarmin(r) ? "⌚" : "📝";
}

function dayLabel(iso) {
  return new Date(iso).toLocaleDateString("en-IN", {
    weekday: "short", day: "numeric", month: "short", timeZone: TZ,
  });
}

function timeLabel(iso) {
  return new Date(iso).toLocaleTimeString("en-IN", {
    hour: "2-digit", minute: "2-digit", timeZone: TZ,
  });
}

function Overview({ records }) {
 const garmin = records
  .filter((r) =>
    ["SLEEP", "HEART_RATE", "ACTIVITY", "CALORIES"].includes(
      (r.recordType || "").toUpperCase()
    )
  )
  .sort(byTimeDesc);
  const reported = records.filter((r) => !isGarmin(r));

  const now = Date.now();
  const weekAgo = now - 7 * 24 * 60 * 60 * 1000;

  const reportedWeek = reported.filter((r) => {
    const time = new Date(r.recordedAt).getTime();
    return time >= weekAgo && time <= now;
  });

  const typeOf = (r) => (r.recordType || "").toUpperCase();

  const latestOf = (words) =>
    garmin.find((r) => words.some((word) => typeOf(r).includes(word)));

  const sleep = latestOf(["SLEEP"]);
  const heart = latestOf(["HEART_RATE", "HEARTRATE"]);
  const steps = latestOf(["STEP", "ACTIVITY"]);
  const calories = latestOf(["CALORIE"]);

  const extract = (record, patterns) => {
    if (!record) return null;

    for (const pattern of patterns) {
      const match = record.content?.match(pattern);
      if (match) return match[1];
    }

    return null;
  };

  const sleepValue =
  extract(sleep, [/Sleep:\s*([\d]+\s*h\s*\d+\s*m)/i]);

const heartValue =
  extract(heart, [/resting\s*([\d.]+\s*bpm)/i]);

const stepsValue =
  extract(steps, [/Steps:\s*([\d,]+)/i]);

const caloriesValue =
  extract(steps, [/([\d,]+)\s*kcal/i]);
  const count = (list, word) =>
    list.filter((r) => typeOf(r).includes(word)).length;

  const lastSymptom = [...reportedWeek]
    .filter((r) => typeOf(r).includes("SYMPTOM"))
    .sort(byTimeDesc)[0];

  const metricCards = [
    {
      title: "Latest sleep",
      value: sleepValue || (sleep ? sleep.content : "No data available"),
      icon: "😴",
      time: sleep?.recordedAt,
    },
    {
      title: "Resting heart rate",
      value: heartValue || (heart ? heart.content : "No data available"),
      icon: "❤️",
      time: heart?.recordedAt,
    },
    {
      title: "Steps",
      value: stepsValue || (steps ? steps.content : "No data available"),
      icon: "👟",
      time: steps?.recordedAt,
    },
    {
      title: "Calories",
      value: caloriesValue || (calories ? calories.content : "No data available"),
      icon: "🔥",
      time: calories?.recordedAt,
    },
  ];

  return (
    <div className="overview">
      <h3>Health overview</h3>

      <div className="ov-label">⌚ Latest Garmin readings</div>

      <div className="ov-grid">
        {metricCards.map((card) => (
          <div className="card garmin-card" key={card.title}>
            <div className="card-title">
              {card.icon} {card.title}
            </div>

            <div className="card-body">{card.value}</div>

            {card.time && (
              <div className="card-time">
                {dayLabel(card.time)}, {timeLabel(card.time)}
              </div>
            )}
          </div>
        ))}
      </div>

      <div className="ov-label">
        🗣️ Reported by you (last 7 days)
      </div>

      <div className="ov-grid">
        <div className="card reported-card">
          <div className="card-title">🤒 Symptoms</div>
          <div className="card-big">{count(reportedWeek, "SYMPTOM")}</div>
        </div>

        <div className="card reported-card">
          <div className="card-title">💊 Medicines and supplements</div>
          <div className="card-big">
            {count(reportedWeek, "MEDIC") +
              count(reportedWeek, "SUPPLEMENT") +
              count(reportedWeek, "VITAMIN")}
          </div>
        </div>

        <div className="card reported-card wide">
          <div className="card-title">Latest symptom</div>
          <div className="card-body">
            {lastSymptom
              ? `${lastSymptom.content} (${dayLabel(lastSymptom.recordedAt)}, ${timeLabel(lastSymptom.recordedAt)})`
              : "None reported in the last 7 days"}
          </div>
        </div>
      </div>
    </div>
  );
}
export default function App() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [records, setRecords] = useState([]);
  const [busy, setBusy] = useState(false);
  const [listening, setListening] = useState(false);
  const [filter, setFilter] = useState("ALL");

  async function loadRecords() {
  try {
    const r = await fetch("/api/V1/healthRecords");

    if (r.ok) {
      const data = await r.json();
      console.log("Health records from backend:", data);
      console.table(
        data
          .filter((record) => record.source === "GARMIN")
          .map((record) => ({
            type: record.recordType,
            content: record.content,
            recordedAt: record.recordedAt,
          }))
      );
      setRecords(data);
    } else {
      console.error("Failed to load records:", r.status);
    }
  } catch (error) {
    console.error("Could not load health records:", error);
  }
}


  useEffect(() => {
    loadRecords();
  }, []);

  function speak(text) {
    if ("speechSynthesis" in window && text) {
      speechSynthesis.speak(new SpeechSynthesisUtterance(text));
    }
  }

  async function send(text, Source= "WEBSITE") {
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
    rec.onresult = (e) => send(e.results[0][0].transcript, "ALEXA");
    rec.start();
  }

  const visible = records
  .filter((r) =>
    filter === "ALL" ? true : filter === "GARMIN" ? isGarmin(r) : !isGarmin(r)
  )
  .sort(byTimeDesc);

const groups = [];
visible.forEach((r) => {
  const day = dayLabel(r.recordedAt);
  const last = groups[groups.length - 1];
  if (last && last.day === day) last.items.push(r);
  else groups.push({ day, items: [r] });
});

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
  <h2>Health dashboard</h2>

  <Overview records={records} />

  <h3>Combined timeline</h3>
  <div className="legend">
    <span className="dot dot-garmin" /> Garmin measurement
    <span className="dot dot-reported" /> Reported by you (Alexa or website)
  </div>
  <div className="filters">
    {["ALL", "REPORTED", "GARMIN"].map((f) => (
      <button
        key={f}
        className={filter === f ? "active" : ""}
        onClick={() => setFilter(f)}
      >
        {f === "ALL" ? "All" : f === "REPORTED" ? "Reported" : "Garmin"}
      </button>
    ))}
  </div>

  {groups.length === 0 && <p className="hint">No records yet.</p>}

  <div className="timeline-scroll">
    {groups.map((g) => (
      <div key={g.day}>
        <div className="day-header">{g.day}</div>
        <ul className="timeline">
          {g.items.map((r, i) => (
            <li key={r.id || i} className={isGarmin(r) ? "row-garmin" : "row-reported"}>
              <div className="when">{timeLabel(r.recordedAt)}</div>
              <div>
                <span className="badge">
                  {icon(r)} {(r.recordType || "").replaceAll("_", " ")}
                </span>{" "}
                {r.content}
                <span className={"src " + r.source}>
                  {isGarmin(r) ? "⌚ Garmin" : r.source === "ALEXA" ? "🎙️ Alexa" : "🌐 Website"}
                </span>
              </div>
            </li>
          ))}
        </ul>
      </div>
    ))}
  </div>
</section>
    </div>
  );
}