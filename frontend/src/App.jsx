import { useState, useCallback, useEffect, useRef } from "react";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

/* ────────────────────────── helpers ────────────────────────── */

function formatNumber(n) {
  if (n === null || n === undefined || n === "") return "0";
  const str = String(n);
  if (str === "Error" || str.startsWith("Error")) return str;
  const num = parseFloat(str);
  if (isNaN(num)) return str;
  if (num === 0) return "0";
  if (Number.isInteger(num) && Math.abs(num) < 1e15) return num.toLocaleString("en-US");
  if (Math.abs(num) >= 1e15 || (Math.abs(num) < 1e-6 && num !== 0)) return num.toExponential(8);
  return parseFloat(num.toPrecision(12)).toString();
}

const OP_MAP = {
  "+": "add",
  "−": "subtract",
  "×": "multiply",
  "÷": "divide",
  "%": "modulo",
  "xʸ": "power",
};

/* ────────────────────────── component ────────────────────────── */

export default function App() {
  const [display, setDisplay] = useState("0");
  const [expression, setExpression] = useState("");
  const [operator, setOperator] = useState(null);
  const [prev, setPrev] = useState(null);
  const [resetNext, setResetNext] = useState(false);
  const [mode, setMode] = useState("basic");
  const [history, setHistory] = useState([]);
  const [angleMode, setAngleMode] = useState("DEG");
  const [memory, setMemory] = useState(0);
  const [memoryIndicator, setMemoryIndicator] = useState(false);
  const [inverse, setInverse] = useState(false);
  const [hyperbolic, setHyperbolic] = useState(false);
  const [ripple, setRipple] = useState(null);
  const rippleTimer = useRef(null);

  /* ── button ripple effect ── */
  const triggerRipple = useCallback((id) => {
    setRipple(id);
    if (rippleTimer.current) clearTimeout(rippleTimer.current);
    rippleTimer.current = setTimeout(() => setRipple(null), 300);
  }, []);

  /* ── digit / decimal input ── */
  const inputDigit = useCallback((digit) => {
    triggerRipple("d" + digit);
    setDisplay((d) => {
      if (resetNext || d === "0" || d === "Error") {
        setResetNext(false);
        return digit;
      }
      if (d.length >= 16) return d;
      return d + digit;
    });
  }, [resetNext, triggerRipple]);

  const inputDecimal = useCallback(() => {
    triggerRipple("decimal");
    setDisplay((d) => {
      if (resetNext) {
        setResetNext(false);
        return "0.";
      }
      if (d.includes(".")) return d;
      return d + ".";
    });
  }, [resetNext, triggerRipple]);

  /* ── call backend ── */
  const callBackend = useCallback(async (op, a, b) => {
    try {
      const res = await fetch(`${API_URL}/api/calculate`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ operation: op, a: parseFloat(a), b: parseFloat(b || 0) }),
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data.error || "Error");
      return data.result;
    } catch (e) {
      return "Error: " + (e.message || "Unknown");
    }
  }, []);

  const callUnary = useCallback(async (op, a) => {
    return callBackend(op, a, 0);
  }, [callBackend]);

  /* ── operator pressed ── */
  const handleOperator = useCallback(async (op) => {
    triggerRipple("op-" + op);
    const current = parseFloat(display);

    if (prev !== null && operator && !resetNext) {
      const backendOp = OP_MAP[operator];
      const result = await callBackend(backendOp, prev, current);
      const exprStr = `${formatNumber(prev)} ${operator} ${formatNumber(current)}`;
      setDisplay(String(result));
      setExpression(exprStr);
      setPrev(String(result).startsWith("Error") ? null : result);
    } else {
      setPrev(current);
    }

    setOperator(op);
    setResetNext(true);
  }, [display, prev, operator, resetNext, callBackend, triggerRipple]);

  /* ── equals ── */
  const handleEquals = useCallback(async () => {
    triggerRipple("equals");
    if (prev === null || !operator) return;
    const current = parseFloat(display);
    const backendOp = OP_MAP[operator];
    const result = await callBackend(backendOp, prev, current);
    const exprStr = `${formatNumber(prev)} ${operator} ${formatNumber(current)} =`;

    setHistory((h) => [{ expr: exprStr, result: formatNumber(result) }, ...h].slice(0, 50));
    setExpression(exprStr);
    setDisplay(String(result));
    setPrev(null);
    setOperator(null);
    setResetNext(true);
  }, [display, prev, operator, callBackend, triggerRipple]);

  /* ── unary scientific operations ── */
  const handleUnary = useCallback(async (op, label) => {
    triggerRipple("sci-" + op);
    const result = await callUnary(op, display);
    const exprStr = `${label || op}(${display})`;
    setHistory((h) => [{ expr: exprStr, result: formatNumber(result) }, ...h].slice(0, 50));
    setExpression(exprStr);
    setDisplay(String(result));
    setResetNext(true);
  }, [display, callUnary, triggerRipple]);

  /* ── trig with angle mode ── */
  const handleTrig = useCallback(async (baseFn) => {
    const fn = angleMode === "DEG" ? baseFn + "deg" : baseFn;
    const label = baseFn + (angleMode === "DEG" ? "°" : "");
    await handleUnary(fn, label);
  }, [angleMode, handleUnary]);

  /* ── constant insertion ── */
  const insertConstant = useCallback(async (op, label) => {
    triggerRipple("const-" + op);
    const result = await callUnary(op, 0);
    setExpression(label);
    setDisplay(String(result));
    setResetNext(true);
  }, [callUnary, triggerRipple]);

  /* ── memory functions ── */
  const memoryAdd = useCallback(() => {
    triggerRipple("mem-add");
    setMemory((m) => m + parseFloat(display));
    setMemoryIndicator(true);
    setResetNext(true);
  }, [display, triggerRipple]);

  const memorySubtract = useCallback(() => {
    triggerRipple("mem-sub");
    setMemory((m) => m - parseFloat(display));
    setMemoryIndicator(true);
    setResetNext(true);
  }, [display, triggerRipple]);

  const memoryRecall = useCallback(() => {
    triggerRipple("mem-recall");
    setDisplay(String(memory));
    setResetNext(true);
  }, [memory, triggerRipple]);

  const memoryClear = useCallback(() => {
    triggerRipple("mem-clear");
    setMemory(0);
    setMemoryIndicator(false);
  }, [triggerRipple]);

  /* ── clear / backspace / toggle sign ── */
  const handleClear = useCallback(() => {
    triggerRipple("clear");
    setDisplay("0");
    setExpression("");
    setOperator(null);
    setPrev(null);
    setResetNext(false);
  }, [triggerRipple]);

  const handleBackspace = useCallback(() => {
    triggerRipple("backspace");
    setDisplay((d) => (d.length <= 1 || d === "Error" || d.startsWith("Error")) ? "0" : d.slice(0, -1));
  }, [triggerRipple]);

  const handleToggleSign = useCallback(() => {
    triggerRipple("toggle-sign");
    setDisplay((d) => {
      if (d === "0" || d === "Error" || d.startsWith("Error")) return d;
      return d.startsWith("-") ? d.slice(1) : "-" + d;
    });
  }, [triggerRipple]);

  /* ── keyboard support ── */
  useEffect(() => {
    const handler = (e) => {
      if (e.key >= "0" && e.key <= "9") inputDigit(e.key);
      else if (e.key === ".") inputDecimal();
      else if (e.key === "+") handleOperator("+");
      else if (e.key === "-") handleOperator("−");
      else if (e.key === "*") handleOperator("×");
      else if (e.key === "/") { e.preventDefault(); handleOperator("÷"); }
      else if (e.key === "%") handleOperator("%");
      else if (e.key === "Enter" || e.key === "=") handleEquals();
      else if (e.key === "Backspace") handleBackspace();
      else if (e.key === "Escape" || e.key === "Delete") handleClear();
      else if (e.key === "p") insertConstant("pi", "π");
      else if (e.key === "e" && !e.ctrlKey) insertConstant("euler", "e");
    };
    window.addEventListener("keydown", handler);
    return () => window.removeEventListener("keydown", handler);
  }, [inputDigit, inputDecimal, handleOperator, handleEquals, handleBackspace, handleClear, insertConstant]);

  /* ── pick display size ── */
  const isError = display === "Error" || display.startsWith("Error");
  const displayClass = isError
    ? "display__value display__value--error"
    : display.length > 12
      ? "display__value display__value--small"
      : "display__value";

  const btnClass = (base, extra) =>
    `btn ${base} ${extra || ""} ${ripple === extra ? "btn--ripple" : ""}`.trim();

  /* ────────────────────── render ────────────────────── */
  return (
    <>
      <div className="scene">
        <div className="orb orb--violet" />
        <div className="orb orb--indigo" />
        <div className="orb orb--cyan" />
        <div className="orb orb--emerald" />
      </div>

      <div className="app">
        <header className="header">
          <h1>⚡ Calculator</h1>
          <p>Powered by Spring Boot + React</p>
        </header>

        <div className="calc">
          {/* indicators bar */}
          <div className="indicators">
            {memoryIndicator && <span className="indicator indicator--memory">M</span>}
            {mode === "scientific" && (
              <>
                <span className={`indicator ${angleMode === "DEG" ? "indicator--active" : ""}`}>DEG</span>
                <span className={`indicator ${angleMode === "RAD" ? "indicator--active" : ""}`}>RAD</span>
                {inverse && <span className="indicator indicator--active">INV</span>}
                {hyperbolic && <span className="indicator indicator--active">HYP</span>}
              </>
            )}
          </div>

          {/* display */}
          <div className="display">
            <div className="display__expression">{expression || "\u00A0"}</div>
            <div className={displayClass}>{formatNumber(display)}</div>
          </div>

          {/* mode toggle */}
          <div className="mode-toggle">
            <button
              className={`mode-toggle__btn ${mode === "basic" ? "mode-toggle__btn--active" : ""}`}
              onClick={() => setMode("basic")}
              id="mode-basic"
            >
              Basic
            </button>
            <button
              className={`mode-toggle__btn ${mode === "scientific" ? "mode-toggle__btn--active" : ""}`}
              onClick={() => setMode("scientific")}
              id="mode-scientific"
            >
              Scientific
            </button>
          </div>

          {/* scientific panel */}
          {mode === "scientific" && (
            <div className="sci-panel">
              {/* ── Row 1: Mode toggles ── */}
              <div className="sci-row">
                <button
                  className={`btn btn--sci-toggle ${inverse ? "btn--sci-toggle--active" : ""}`}
                  onClick={() => setInverse((v) => !v)}
                  id="btn-inv"
                >INV</button>
                <button
                  className={`btn btn--sci-toggle ${hyperbolic ? "btn--sci-toggle--active" : ""}`}
                  onClick={() => setHyperbolic((v) => !v)}
                  id="btn-hyp"
                >HYP</button>
                <button
                  className={`btn btn--sci-toggle ${angleMode === "DEG" ? "btn--sci-toggle--active" : ""}`}
                  onClick={() => setAngleMode((m) => m === "DEG" ? "RAD" : "DEG")}
                  id="btn-angle"
                >{angleMode}</button>
              </div>

              {/* ── Row 2: Trig ── */}
              <div className="sci-row">
                {!hyperbolic && !inverse && (
                  <>
                    <button className="btn btn--sci" onClick={() => handleTrig("sin")} id="btn-sin">sin</button>
                    <button className="btn btn--sci" onClick={() => handleTrig("cos")} id="btn-cos">cos</button>
                    <button className="btn btn--sci" onClick={() => handleTrig("tan")} id="btn-tan">tan</button>
                  </>
                )}
                {!hyperbolic && inverse && (
                  <>
                    <button className="btn btn--sci" onClick={() => handleTrig("asin")} id="btn-asin">sin⁻¹</button>
                    <button className="btn btn--sci" onClick={() => handleTrig("acos")} id="btn-acos">cos⁻¹</button>
                    <button className="btn btn--sci" onClick={() => handleTrig("atan")} id="btn-atan">tan⁻¹</button>
                  </>
                )}
                {hyperbolic && !inverse && (
                  <>
                    <button className="btn btn--sci" onClick={() => handleUnary("sinh", "sinh")} id="btn-sinh">sinh</button>
                    <button className="btn btn--sci" onClick={() => handleUnary("cosh", "cosh")} id="btn-cosh">cosh</button>
                    <button className="btn btn--sci" onClick={() => handleUnary("tanh", "tanh")} id="btn-tanh">tanh</button>
                  </>
                )}
                {hyperbolic && inverse && (
                  <>
                    <button className="btn btn--sci" onClick={() => handleUnary("sinh", "sinh")} id="btn-sinh-inv">sinh⁻¹</button>
                    <button className="btn btn--sci" onClick={() => handleUnary("cosh", "cosh")} id="btn-cosh-inv">cosh⁻¹</button>
                    <button className="btn btn--sci" onClick={() => handleUnary("tanh", "tanh")} id="btn-tanh-inv">tanh⁻¹</button>
                  </>
                )}
              </div>

              {/* ── Row 3: Logs & Exp ── */}
              <div className="sci-row">
                <button className="btn btn--sci" onClick={() => handleUnary("ln", "ln")} id="btn-ln">ln</button>
                <button className="btn btn--sci" onClick={() => handleUnary("log10", "log₁₀")} id="btn-log10">log₁₀</button>
                <button className="btn btn--sci" onClick={() => handleUnary("log2", "log₂")} id="btn-log2">log₂</button>
                <button className="btn btn--sci" onClick={() => handleUnary("exp", "eˣ")} id="btn-exp">eˣ</button>
                <button className="btn btn--sci" onClick={() => handleUnary("tenpow", "10ˣ")} id="btn-10x">10ˣ</button>
              </div>

              {/* ── Row 4: Powers & Roots ── */}
              <div className="sci-row">
                <button className="btn btn--sci" onClick={() => handleUnary("square", "x²")} id="btn-square">x²</button>
                <button className="btn btn--sci" onClick={() => handleUnary("cube", "x³")} id="btn-cube">x³</button>
                <button className="btn btn--sci" onClick={() => handleOperator("xʸ")} id="btn-power">xʸ</button>
                <button className="btn btn--sci" onClick={() => handleUnary("sqrt", "√")} id="btn-sqrt">√x</button>
                <button className="btn btn--sci" onClick={() => handleUnary("cbrt", "∛")} id="btn-cbrt">∛x</button>
              </div>

              {/* ── Row 5: Utility ── */}
              <div className="sci-row">
                <button className="btn btn--sci" onClick={() => handleUnary("factorial", "!")} id="btn-factorial">n!</button>
                <button className="btn btn--sci" onClick={() => handleUnary("reciprocal", "1/x")} id="btn-reciprocal">1/x</button>
                <button className="btn btn--sci" onClick={() => handleUnary("abs", "|x|")} id="btn-abs">|x|</button>
                <button className="btn btn--sci" onClick={() => handleUnary("percentage", "%")} id="btn-percent">%</button>
              </div>

              {/* ── Row 6: Constants & Memory ── */}
              <div className="sci-row">
                <button className="btn btn--sci btn--const" onClick={() => insertConstant("pi", "π")} id="btn-pi">π</button>
                <button className="btn btn--sci btn--const" onClick={() => insertConstant("euler", "e")} id="btn-euler">e</button>
                <button className="btn btn--sci btn--mem" onClick={memoryClear} id="btn-mc">MC</button>
                <button className="btn btn--sci btn--mem" onClick={memoryRecall} id="btn-mr">MR</button>
                <button className="btn btn--sci btn--mem" onClick={memoryAdd} id="btn-mplus">M+</button>
                <button className="btn btn--sci btn--mem" onClick={memorySubtract} id="btn-mminus">M−</button>
              </div>
            </div>
          )}

          {/* main buttons */}
          <div className="buttons">
            <button className="btn btn--danger" onClick={handleClear} id="btn-clear">AC</button>
            <button className="btn btn--danger" onClick={handleBackspace} id="btn-backspace">⌫</button>
            <button className="btn btn--operator" onClick={handleToggleSign} id="btn-toggle-sign">±</button>
            <button className={`btn btn--operator ${operator === "÷" ? "active" : ""}`} onClick={() => handleOperator("÷")} id="btn-divide">÷</button>

            <button className="btn btn--number" onClick={() => inputDigit("7")} id="btn-7">7</button>
            <button className="btn btn--number" onClick={() => inputDigit("8")} id="btn-8">8</button>
            <button className="btn btn--number" onClick={() => inputDigit("9")} id="btn-9">9</button>
            <button className={`btn btn--operator ${operator === "×" ? "active" : ""}`} onClick={() => handleOperator("×")} id="btn-multiply">×</button>

            <button className="btn btn--number" onClick={() => inputDigit("4")} id="btn-4">4</button>
            <button className="btn btn--number" onClick={() => inputDigit("5")} id="btn-5">5</button>
            <button className="btn btn--number" onClick={() => inputDigit("6")} id="btn-6">6</button>
            <button className={`btn btn--operator ${operator === "−" ? "active" : ""}`} onClick={() => handleOperator("−")} id="btn-subtract">−</button>

            <button className="btn btn--number" onClick={() => inputDigit("1")} id="btn-1">1</button>
            <button className="btn btn--number" onClick={() => inputDigit("2")} id="btn-2">2</button>
            <button className="btn btn--number" onClick={() => inputDigit("3")} id="btn-3">3</button>
            <button className={`btn btn--operator ${operator === "+" ? "active" : ""}`} onClick={() => handleOperator("+")} id="btn-add">+</button>

            <button className="btn btn--number btn--wide" onClick={() => inputDigit("0")} id="btn-0">0</button>
            <button className="btn btn--number" onClick={inputDecimal} id="btn-decimal">.</button>
            <button className="btn btn--equals" onClick={handleEquals} id="btn-equals">=</button>
          </div>
        </div>

        {/* history */}
        <div className="history">
          <div className="history__header">
            <div className="history__title">History</div>
            {history.length > 0 && (
              <button className="history__clear" onClick={() => setHistory([])}>Clear</button>
            )}
          </div>
          {history.length === 0 ? (
            <div className="history__empty">No calculations yet. Start computing!</div>
          ) : (
            history.map((item, i) => (
              <div
                key={i}
                className="history__item"
                onClick={() => {
                  const val = parseFloat(item.result.replace(/,/g, ""));
                  setDisplay(isNaN(val) ? "0" : String(val));
                  setResetNext(true);
                }}
              >
                <span className="history__expr">{item.expr}</span>
                <span className="history__result">= {item.result}</span>
              </div>
            ))
          )}
        </div>
      </div>
    </>
  );
}
