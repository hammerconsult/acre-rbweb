UPDATE REGISTRODEOBITO rdo
SET CARTORIO = (select pj.RAZAOSOCIAL
                from REGISTRODEOBITO r
                         inner join CARTORIO c on r.CARTORIO_ID = c.ID
                         inner join PESSOAJURIDICA pj on c.PESSOAJURIDICA_ID = pj.ID
                where r.id = rdo.id);
