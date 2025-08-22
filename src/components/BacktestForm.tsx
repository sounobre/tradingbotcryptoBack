import React, { useState } from 'react';

const BacktestForm: React.FC = () => {
  const [symbol, setSymbol] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [initialCapital, setInitialCapital] = useState<number>(10000);
  const [positionSize, setPositionSize] = useState<number>(1);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    await fetch('/api/backtests', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        symbol,
        startDate,
        endDate,
        initialCapital,
        positionSize,
      }),
    });
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label>
          Símbolo:
          <input
            type="text"
            value={symbol}
            onChange={(e) => setSymbol(e.target.value)}
          />
        </label>
      </div>
      <div>
        <label>
          Data Inicial:
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
          />
        </label>
      </div>
      <div>
        <label>
          Data Final:
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
          />
        </label>
      </div>
      <div>
        <label>
          Capital Inicial:
          <input
            type="number"
            value={initialCapital}
            onChange={(e) => setInitialCapital(Number(e.target.value))}
          />
        </label>
      </div>
      <div>
        <label>
          Tamanho da Posição:
          <input
            type="number"
            value={positionSize}
            onChange={(e) => setPositionSize(Number(e.target.value))}
          />
        </label>
      </div>
      <button type="submit">Iniciar Backtest</button>
    </form>
  );
};

export default BacktestForm;
