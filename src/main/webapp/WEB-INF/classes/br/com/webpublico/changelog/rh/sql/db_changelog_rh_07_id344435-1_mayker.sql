UPDATE FOLHADEPAGAMENTO f SET EFETIVADAEM = (SELECT fa.EFETIVADAEM FROM FOLHADEPAGAMENTO_AUD fa
                                             WHERE fa.ID = f.ID
                                               AND fa.REVTYPE = 1
                                               AND fa.VERSAO = f.VERSAO
                                             ORDER BY fa.REV desc
                                             offset 1 rows fetch next 1 rows only)
WHERE (SELECT COUNT(1) FROM (SELECT fa.EFETIVADAEM AS EFETIVADAEM FROM FOLHADEPAGAMENTO_AUD fa
                             WHERE fa.ID = f.ID
                               AND fa.REVTYPE = 1
                               AND fa.VERSAO = f.VERSAO
                             ORDER BY fa.REV DESC
                             offset 0 rows fetch next 2 rows only) WHERE EFETIVADAEM IS NOT NULL) = 2
