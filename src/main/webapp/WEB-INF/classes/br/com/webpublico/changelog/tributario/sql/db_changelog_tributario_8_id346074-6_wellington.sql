call proc_transfere_ponto_comercial(1004, 101, 102);
call proc_transfere_ponto_comercial(1004, 105, 109);
call proc_transfere_ponto_comercial(1004, 106, 109);
call proc_transfere_ponto_comercial(1004, 107, 109);
call proc_transfere_ponto_comercial(1004, 108, 109);
call proc_transfere_ponto_comercial(1004, 110, 109);
call proc_transfere_ponto_comercial(1004, 111, 102);
call proc_transfere_ponto_comercial(1004, 112, 115);
call proc_transfere_ponto_comercial(1004, 113, 115);
call proc_transfere_ponto_comercial(1004, 114, 115);
call proc_transfere_ponto_comercial(1004, 116, 118);
call proc_transfere_ponto_comercial(1004, 117, 118);
call proc_transfere_ponto_comercial(1004, 121, 100);
call proc_transfere_ponto_comercial(1004, 122, 100);
call proc_transfere_ponto_comercial(1004, 123, 102);
call proc_transfere_ponto_comercial(1004, 124, 103);
call proc_transfere_ponto_comercial(1004, 125, 104);
call proc_transfere_ponto_comercial(1004, 127, 115);
call proc_transfere_ponto_comercial(1004, 128, 118);
call proc_transfere_ponto_comercial(1004, 129, 119);
call proc_transfere_ponto_comercial(1004, 131, 120);
call proc_transfere_ponto_comercial(1004, 132, 115);
call proc_transfere_ponto_comercial(1004, 133, 102);
call proc_transfere_ponto_comercial(1004, 201, 200);
call proc_transfere_ponto_comercial(1004, 300, -1);
call proc_transfere_ponto_comercial(1004, 301, -1);
call proc_transfere_ponto_comercial(1004, 302, -1);
call proc_transfere_ponto_comercial(1004, 303, -1);
call proc_transfere_ponto_comercial(1004, 304, -1);
call proc_transfere_ponto_comercial(1004, 308, -1);
call proc_transfere_ponto_comercial(1004, 321, -1);
call proc_transfere_ponto_comercial(1004, 401, 400);
call proc_transfere_ponto_comercial(1004, 403, 402);
call proc_transfere_ponto_comercial(1004, 502, 501);
call proc_transfere_ponto_comercial(1004, 503, 501);
call proc_transfere_ponto_comercial(1004, 511, -1);
call proc_transfere_ponto_comercial(1004, 512, -1);
call proc_transfere_ponto_comercial(1004, 515, 516);
call proc_transfere_ponto_comercial(1004, 518, 516);
call proc_transfere_ponto_comercial(1004, 519, 310);
call proc_transfere_ponto_comercial(1004, 520, -1);
call proc_transfere_ponto_comercial(1004, 526, -1);
call proc_transfere_ponto_comercial(1004, 528, -1);
call proc_transfere_ponto_comercial(1004, 534, 315);
call proc_transfere_ponto_comercial(1004, 538, 504);
call proc_transfere_ponto_comercial(1004, 538, 504);
call proc_transfere_ponto_comercial(1004, 541, -1);
call proc_transfere_ponto_comercial(1004, 542, -1);
call proc_transfere_ponto_comercial(1004, 544, 109);
call proc_transfere_ponto_comercial(1004, 546, 545);
call proc_transfere_ponto_comercial(1004, 547, 545);
call proc_transfere_ponto_comercial(1004, 548, 104);
call proc_transfere_ponto_comercial(1004, 549, 104);
call proc_transfere_ponto_comercial(1004, 550, 104);
call proc_transfere_ponto_comercial(1004, 551, 109);
call proc_transfere_ponto_comercial(1004, 519, -1);
call proc_transfere_ponto_comercial(1004, 530, 104);

update pontocomercial
set ativo = 0
where id in (select s.id
             from pontocomercial s
                      inner join localizacao l on l.id = s.LOCALIZACAO_ID
                      inner join lotacaovistoriadora lv on lv.id = l.LOTACAOVISTORIADORA_ID
             where lv.codigo = 1004
               and l.codigo = 309
               and s.NUMEROBOX not in ('05', '10'));
