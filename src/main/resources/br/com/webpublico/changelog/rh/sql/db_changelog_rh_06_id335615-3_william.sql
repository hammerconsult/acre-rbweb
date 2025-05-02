update contratofp
set segmentoatuacao = 'PRE_ESCOLA'
where id in (
    select distinct vinculo.id
    from vinculofp vinculo
             inner join contratofp contrato on vinculo.ID = contrato.ID
             inner join LOTACAOFUNCIONAL lotacao on vinculo.ID = lotacao.VINCULOFP_ID
    where lotacao.INICIOVIGENCIA = (select max(lotacaosub.INICIOVIGENCIA)
                                    from LOTACAOFUNCIONAL lotacaosub
                                    where lotacaosub.VINCULOFP_ID = vinculo.id
    )
      and lotacao.UNIDADEORGANIZACIONAL_ID in
          (58757559, 58757558, 58757252, 638875131, 58758539, 58758541,
           638875164, 58758563, 58758542, 153787480, 58758543, 58758535, 58757837,
           522669434, 10651503736, 58758546, 58758011, 58757714, 58757555,
           779786987, 638875140, 58758545, 58758536, 58757696, 414312202, 638875142,
           58758549, 638875144, 58758550, 638875146, 58758551, 58758552, 638875148,
           58757073, 140778329, 58758008, 58757554, 58757247, 638875129, 58758537,
           638875133, 153787433,
           638875152, 58758554, 638875154, 58758555, 58758556, 58758022, 58757722,
           58757567, 58758544, 58758016, 58757561, 58757414, 58758547, 58758018,
           58757719, 58757563,
           638875156, 58758557, 778233781, 638875160, 58758559, 58758608, 58757790,
           58756975,
           58758560, 638875240, 584889240
              )
);

update contratofp
set segmentoatuacao = 'FUNDAMENTAL_1'
where id in (
    select distinct vinculo.id
    from vinculofp vinculo
             inner join contratofp contrato on vinculo.ID = contrato.ID
             inner join LOTACAOFUNCIONAL lotacao on vinculo.ID = lotacao.VINCULOFP_ID
    where lotacao.INICIOVIGENCIA = (select max(lotacaosub.INICIOVIGENCIA)
                                    from LOTACAOFUNCIONAL lotacaosub
                                    where lotacaosub.VINCULOFP_ID = vinculo.id
    )
      and lotacao.UNIDADEORGANIZACIONAL_ID in
          (58758572, 58758569, 58758573, 58758568, 58757866, 58757717, 58757560,
           414311006, 58758592, 58757170, 58757065, 573641069, 58758593, 58758036,
           58758594, 58757874, 58757423, 58757268, 414311955, 153787352, 153787348,
           58758574, 414312624, 58758575, 58758086,
           58757475, 58757320, 58758588, 58757562, 58757415, 58757254, 58758595,
           58758596, 58757573, 58758587, 58758577, 58757870, 58757257, 58757109,
           58758589, 58758564, 58757838, 58758580, 58758565,
           58758576, 58758014, 58757250, 58757106, 58758581, 58757867, 58757413,
           58757253, 8758590, 58758582, 58757869, 58757868, 58757718,
           58758553, 638875150, 58758597, 58757729, 58757570, 58756973,
           58758591, 58758584, 58758571, 58758013, 58757405, 58757103,
           58758566, 58758585, 58758567, 58758599, 58758601, 58758028, 58757569,
           58757114, 58758602, 58758027, 58756969, 58756968,
           58758586, 58758017, 58757108, 58757107, 58758605, 58757115,
           58758598, 58756977, 58758600, 58757572, 58758607, 58757427, 58758604
              )
);

update contratofp
set segmentoatuacao = 'CRECHE'
where id in (
    select distinct vinculo.id
    from vinculofp vinculo
             inner join contratofp contrato on vinculo.ID = contrato.ID
             inner join LOTACAOFUNCIONAL lotacao on vinculo.ID = lotacao.VINCULOFP_ID
    where lotacao.INICIOVIGENCIA = (select max(lotacaosub.INICIOVIGENCIA)
                                    from LOTACAOFUNCIONAL lotacaosub
                                    where lotacaosub.VINCULOFP_ID = vinculo.id
    )
      and lotacao.UNIDADEORGANIZACIONAL_ID in
          (10651504139, 10651503971,
           779891824, 544384411,
           579970797, 779799940, 778236030, 579970713, 522670340, 542952833,
           829141052, 58758525,
           58758526, 58758527, 638875760, 58758579, 58758532, 58758529, 58758530,
           779892845, 779892202, 58758533, 58758534, 638875158, 58758558
              )
);
