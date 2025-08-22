# tradingbotcryptoBack

## Endpoints

### `GET /api/candles`
- **Descrição:** Retorna candles em ordem cronológica.
- **Parâmetros de query:**
  - `symbol` (obrigatório): par de ativos, por exemplo `BTCUSDT`.
  - `interval` (obrigatório): intervalo do candle conforme a API da corretora.
  - `limit` (opcional, padrão 500): quantidade máxima de candles retornados.
- **Resposta:** Lista de objetos com `openTime`, `closeTime`, `open`, `high`, `low`, `close` e `volume`.

### `POST /api/backtests`
- **Descrição:** Executa um backtest para uma estratégia usando candles armazenados.
- **Corpo JSON:**
  - `symbol` (obrigatório) – par de ativos.
  - `interval` (obrigatório) – intervalo do candle.
  - `limit` (opcional, padrão 500) – quantidade de candles usados.
  - `strategy` (obrigatório) – nome da estratégia.
  - `fast` (opcional, padrão 12) – período curto para estratégias de médias móveis.
  - `slow` (opcional, padrão 26) – período longo.
  - `rsi` (opcional, padrão 14) – período do RSI.
  - `bb` (opcional, padrão 20) – período das Bandas de Bollinger.
- **Resposta:** Objeto com `metrics` (totalReturnPct, maxDrawdownPct, winRatePct, profitFactor, trades), `equity` (lista de {time, value}) e `trades` (lista de {time, type, price}).

### `POST /api/ingest`
- **Descrição:** Busca candles recentes do provedor e salva no banco.
- **Corpo JSON:**
  - `symbol` (obrigatório) – par de ativos.
  - `interval` (obrigatório) – intervalo do candle.
  - `limit` (opcional, padrão 500) – quantidade de candles a buscar.
- **Resposta:** Objeto `{ "saved": <numero de candles salvos> }`.

