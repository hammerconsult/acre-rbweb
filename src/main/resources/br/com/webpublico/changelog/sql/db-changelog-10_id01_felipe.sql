-- Parece mas não é, o enum estava errado, antes era AT0_DE_CARGO (AT0 <- (zero))
update atolegal set propositoatolegal = 'ATO_DE_CARGO' where propositoatolegal = 'AT0_DE_CARGO';
commit;