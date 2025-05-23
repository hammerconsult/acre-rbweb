INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZC'),
        (select id from classificacaouso where categoria = 'CSI 1 - Comércio, Serviços e Instituições'), null,
        (select id from hierarquiavia where descricao = 'Local Principal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZC'),
        (select id from classificacaouso where categoria = 'CSI 2 - Comércio, Serviços e Instituições'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZC'),
        (select id from classificacaouso where categoria = 'PGT 1 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Federal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZC'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Federal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZC'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Federal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZC'),
        (select id from classificacaouso where categoria = 'PGT 2 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZC'),
        (select id from classificacaouso where categoria = 'PGT 3 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZC'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZC'),
        (select id from classificacaouso where categoria = 'AGF - Agroflorestal'),
        (select id from classificacaousoitem where codigo = 366.0), null, 0);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZC'),
        (select id from classificacaouso where categoria = 'AGF - Agroflorestal'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZEC'),
        (select id from classificacaouso where categoria = 'CSI 1 - Comércio, Serviços e Instituições'), null,
        (select id from hierarquiavia where descricao = 'Local Principal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZEC'),
        (select id from classificacaouso where categoria = 'CSI 2 - Comércio, Serviços e Instituições'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Federal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZEC'),
        (select id from classificacaouso where categoria = 'PGT 1 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Federal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZEC'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Federal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZEC'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Federal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZEC'),
        (select id from classificacaouso where categoria = 'PGT 2 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZEC'),
        (select id from classificacaouso where categoria = 'PGT 2 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZEC'),
        (select id from classificacaouso where categoria = 'PGT 3 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZEC'),
        (select id from classificacaouso where categoria = 'PGT 3 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZEC'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZEC'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZEC'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZEC'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZEC'),
        (select id from classificacaouso where categoria = 'AGF - Agroflorestal'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZQU'),
        (select id from classificacaouso where categoria = 'CSI 2 - Comércio, Serviços e Instituições'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZQU'),
        (select id from classificacaouso where categoria = 'CSI 1 - Comércio, Serviços e Instituições'), null,
        (select id from hierarquiavia where descricao = 'Local Principal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZQU'),
        (select id from classificacaouso where categoria = 'PGT 1 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Estadual'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZQU'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Estadual'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZQU'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Estadual'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZQU'),
        (select id from classificacaouso where categoria = 'PGT 2 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZQU'),
        (select id from classificacaouso where categoria = 'PGT 2 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZQU'),
        (select id from classificacaouso where categoria = 'PGT 3 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZQU'),
        (select id from classificacaouso where categoria = 'PGT 3 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZQU'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZQU'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZQU'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZQU'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZQU'),
        (select id from classificacaouso where categoria = 'AGF - Agroflorestal'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'CSI 2 - Comércio, Serviços e Instituições'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'CSI 1 - Comércio, Serviços e Instituições'), null,
        (select id from hierarquiavia where descricao = 'Local Principal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'PGT 1 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Federal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'PGT 1 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Estadual'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Federal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Estadual'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Federal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Estadual'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'PGT 2 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'PGT 2 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'PGT 3 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'PGT 3 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZOT'),
        (select id from classificacaouso where categoria = 'AGF - Agroflorestal'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAA'), null, null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAM'),
        (select id from classificacaouso where categoria = 'CSI 2 - Comércio, Serviços e Instituições'), null,
        (select id from hierarquiavia where descricao = 'Local Principal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAM'),
        (select id from classificacaouso where categoria = 'AGF - Agroflorestal'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'CSI 2 - Comércio, Serviços e Instituições'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'CSI 1 - Comércio, Serviços e Instituições'), null,
        (select id from hierarquiavia where descricao = 'Local Principal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'PGT 2 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'PGT 2 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'PGT 2 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Federal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'PGT 2 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Estadual'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'PGT 3 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'PGT 3 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'PGT 3 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Federal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'PGT 3 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Estadual'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Federal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Estadual'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Federal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Estadual'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'PGT 1 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Federal'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'PGT 1 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Rodovia Estadual'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZVAB'),
        (select id from classificacaouso where categoria = 'AGF - Agroflorestal'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZIHCPAPAC'),
        (select id from classificacaouso where categoria = 'CSI 1 - Comércio, Serviços e Instituições'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZIHCPAPAC'),
        (select id from classificacaouso where categoria = 'PGT 2 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZIHCPAPAC'),
        (select id from classificacaouso where categoria = 'PGT 3 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZIHCPAPAC'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZIHCPAPAC'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZIHCPPCAU'),
        (select id from classificacaouso where categoria = 'CSI 1 - Comércio, Serviços e Instituições'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZIHCPPCAU'),
        (select id from classificacaouso where categoria = 'CSI 2 - Comércio, Serviços e Instituições'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZIHCPPCAU'),
        (select id from classificacaouso where categoria = 'PGT 2 - Polo Gerador de Tráfego'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZIHCPPCAU'),
        (select id from classificacaouso where categoria = 'PGT 3 - Polo Gerador de Tráfego'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZIHCPPCAU'),
        (select id from classificacaouso where categoria = 'GRD - Gerador de Ruído Diurno'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZIHCPPCAU'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZIHCPPC'),
        (select id from classificacaouso where categoria = 'CSI 1 - Comércio, Serviços e Instituições'), null, null, 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZIHCPPC'),
        (select id from classificacaouso where categoria = 'PGT 2 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZIHCPPC'),
        (select id from classificacaouso where categoria = 'PGT 2 - Polo Gerador de Tráfego'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZIHCPPC'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Arterial'), 1);
INSERT INTO PERMISSAOUSOZONEAMENTO (ID, ZONA_ID, CLASSIFICACAOUSO_ID, CLASSIFICACAOUSOITEM_ID, HIERARQUIAVIA_ID,
                                    PERMITIDO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, (select id from zona where sigla = 'ZIHCPPC'),
        (select id from classificacaouso where categoria = 'GRN - Gerador de Ruído Noturno'), null,
        (select id from hierarquiavia where descricao = 'Coletora'), 1);
