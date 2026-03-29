 // ─── UNIT DATA ───
  const units = {
    length: {
      Kilometer:  1000, Meter: 1, Centimeter: 0.01,
      Millimeter: 0.001, Mile: 1609.34, Foot: 0.3048, Inch: 0.0254
    },
    weight: {
      Kilogram: 1, Gram: 0.001, Milligram: 0.000001,
      Pound: 0.453592, Ounce: 0.0283495, Ton: 1000
    },
    temperature: { Celsius: 'C', Fahrenheit: 'F', Kelvin: 'K' },
    volume: {
      Liter: 1, Milliliter: 0.001, 'Fluid Ounce': 0.0295735,
      Cup: 0.236588, Gallon: 3.78541
    }
  };

  let activeType = 'length';
  let activeOp = '+';

  function selectType(el, type) {
    document.querySelectorAll('.type-card').forEach(c => c.classList.remove('active'));
    el.classList.add('active');
    activeType = type;
    updateUnits();
    runComparison();
    runConversion();
    runArith();
  }

  function updateUnits() {
    const typeUnits = {
      length: ['Kilometer','Meter','Centimeter','Millimeter','Mile','Foot','Inch'],
      weight: ['Kilogram','Gram','Milligram','Pound','Ounce','Ton'],
      temperature: ['Celsius','Fahrenheit','Kelvin'],
      volume: ['Liter','Milliliter','Fluid Ounce','Cup','Gallon']
    };
    const list = typeUnits[activeType] || [];

    ['cmpFromUnit','cmpToUnit','convFromUnit'].forEach(id => {
      const sel = document.getElementById(id);
      if (!sel) return;
      sel.innerHTML = list.map(u => `<option>${u}</option>`).join('');
    });
    if (document.getElementById('cmpToUnit') && list[1]) document.getElementById('cmpToUnit').selectedIndex = 1;

    ['arithAUnit','arithBUnit'].forEach(id => {
      const sel = document.getElementById(id);
      if (!sel) return;
      const short = { length:['m','km','cm','mm'], weight:['kg','g','mg','lb'], temperature:['°C','°F','K'], volume:['L','mL','fl oz','cup'] };
      sel.innerHTML = (short[activeType]||[]).map(u => `<option>${u}</option>`).join('');
    });
  }

  function switchTab(btn, tab) {
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('visible'));
    document.getElementById('tab-' + tab).classList.add('visible');
  }

  function selectOp(el, op) {
    document.querySelectorAll('.op-btn').forEach(b => b.classList.remove('active'));
    el.classList.add('active');
    activeOp = op;
    runArith();
  }

  function toBase(val, unit) {
    const map = units[activeType];
    if (!map) return val;
    const factor = map[unit];
    if (factor === undefined) return val;
    if (typeof factor === 'string') return convertTemp(val, unit, 'base');
    return val * factor;
  }

  function convertTemp(val, fromUnit, toUnit) {
    let c;
    if (fromUnit === 'Celsius') c = val;
    else if (fromUnit === 'Fahrenheit') c = (val - 32) * 5/9;
    else c = val - 273.15;
    if (toUnit === 'Celsius') return c;
    if (toUnit === 'Fahrenheit') return c * 9/5 + 32;
    return c + 273.15;
  }

  function runComparison() {
    const a = parseFloat(document.getElementById('cmpFrom').value) || 0;
    const b = parseFloat(document.getElementById('cmpTo').value) || 0;
    const ua = document.getElementById('cmpFromUnit').value;
    const ub = document.getElementById('cmpToUnit').value;

    let ba, bb;
    if (activeType === 'temperature') {
      ba = convertTemp(a, ua, 'Celsius');
      bb = convertTemp(b, ub, 'Celsius');
    } else {
      ba = toBase(a, ua);
      bb = toBase(b, ub);
    }

    const sym = ba < bb ? '<' : ba > bb ? '>' : '=';
    const desc = ba < bb ? `${a} ${ua} is less than ${b} ${ub}` :
                 ba > bb ? `${a} ${ua} is greater than ${b} ${ub}` :
                 `${a} ${ua} equals ${b} ${ub}`;

    document.getElementById('cmpSymbol').textContent = sym;
    document.getElementById('cmpDesc').textContent = desc;
  }

  function runConversion() {
    const val = parseFloat(document.getElementById('convVal').value) || 0;
    const fromUnit = document.getElementById('convFromUnit').value;

    const typeUnits = {
      length: ['Kilometer','Meter','Centimeter','Millimeter','Mile','Foot','Inch'],
      weight: ['Kilogram','Gram','Milligram','Pound','Ounce','Ton'],
      temperature: ['Celsius','Fahrenheit','Kelvin'],
      volume: ['Liter','Milliliter','Fluid Ounce','Cup','Gallon']
    };

    const targets = (typeUnits[activeType] || []).filter(u => u !== fromUnit);
    const container = document.getElementById('convResults');
    container.innerHTML = '';

    targets.slice(0,6).forEach(toUnit => {
      let result;
      if (activeType === 'temperature') {
        result = convertTemp(val, fromUnit, toUnit);
      } else {
        const baseVal = toBase(val, fromUnit);
        const toFactor = units[activeType][toUnit];
        result = baseVal / toFactor;
      }
      const formatted = Math.abs(result) >= 1000 || (Math.abs(result) > 0 && Math.abs(result) < 0.001)
        ? result.toExponential(3) : parseFloat(result.toFixed(6));

      const row = document.createElement('div');
      row.style.cssText = 'display:flex;justify-content:space-between;align-items:center;padding:10px 14px;background:#F8F7F5;border-radius:10px;font-size:14px;';
      row.innerHTML = `<span style="color:var(--sub)">${toUnit}</span><span style="font-weight:700;color:var(--red)">${formatted}</span>`;
      container.appendChild(row);
    });
  }

  function runArith() {
    const a = parseFloat(document.getElementById('arithA').value) || 0;
    const b = parseFloat(document.getElementById('arithB').value) || 0;
    const uA = document.getElementById('arithAUnit').value;
    const uB = document.getElementById('arithBUnit').value;
    const opSymbols = {'+':'+','-':'−','*':'×','/':'÷'};

    let result;
    if (activeOp === '+') result = a + b;
    else if (activeOp === '-') result = a - b;
    else if (activeOp === '*') result = a * b;
    else result = b !== 0 ? a / b : NaN;

    const resStr = isNaN(result) ? '∞' : parseFloat(result.toFixed(6));
    document.getElementById('arithResult').textContent = resStr;
    document.getElementById('arithUnit').textContent = uA;
    document.getElementById('arithEq').textContent = `${a} ${uA} ${opSymbols[activeOp]} ${b} ${uB} = ${resStr} ${uA}`;
  }

  // init
  runComparison();
  runConversion();
  runArith();

