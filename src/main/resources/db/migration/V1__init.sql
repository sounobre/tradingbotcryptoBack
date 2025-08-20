CREATE TABLE IF NOT EXISTS candles (
                                       id BIGSERIAL PRIMARY KEY,
                                       symbol VARCHAR(20) NOT NULL,
                                       interval VARCHAR(10) NOT NULL,
                                       open_time TIMESTAMPTZ NOT NULL,
                                       close_time TIMESTAMPTZ NOT NULL,
                                       open NUMERIC(18,8) NOT NULL,
                                       high NUMERIC(18,8) NOT NULL,
                                       low NUMERIC(18,8) NOT NULL,
                                       close NUMERIC(18,8) NOT NULL,
                                       volume NUMERIC(30,10) NOT NULL,
                                       UNIQUE(symbol, interval, open_time)
);
CREATE INDEX IF NOT EXISTS idx_candles_sym_int_time ON candles(symbol, interval, open_time);