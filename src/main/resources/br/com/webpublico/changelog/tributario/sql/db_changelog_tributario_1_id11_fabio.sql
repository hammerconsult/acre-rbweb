UPDATE DIVIDA SET isparcelamento = 0;
UPDATE DIVIDA SET isparcelamento = 1 WHERE descricao like '%PARCELA%';
