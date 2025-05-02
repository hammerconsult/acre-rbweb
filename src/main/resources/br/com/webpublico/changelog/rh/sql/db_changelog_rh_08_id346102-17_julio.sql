update configuracaoaposentadoria c set c.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária Integral Comum (Art. 3º, CF, NR EC 47/2005)')
where c.regraaposentadoria = 'VOLUNTARIA_INTEGRAL_COMNUM_ART_2005';

update configuracaoaposentadoria c set c.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária Integral Comum(transição - Art.6º, EC 41/2003)')
where c.regraaposentadoria = 'VOLUNTARIA_INTEGRAL_COMNUM_TRANSICAO_ART6_2003';

update configuracaoaposentadoria c set c.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária Integral Especial(transição - Art.6º, EC 41/2003)')
where c.regraaposentadoria = 'VOLUNTARIA_INTEGRAL_ESPECIAL_MAGISTERIO_ART6_2003';

update configuracaoaposentadoria c set c.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária Integral Comum(transição - Art.2º, EC 41/2003)')
where c.regraaposentadoria = 'VOLUNTARIA_INTEGRAL_COMUM_TRANSICAO_ART2_2003';

update configuracaoaposentadoria c set c.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária Integral Especial(transição - Art.2º, EC 41/2003)')
where c.regraaposentadoria = 'VOLUNTARIA_INTEGRAL_ESPECIAL_TRANSICAO_ART2_2003';

update configuracaoaposentadoria c set c.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Invalidez(Art.40, EC, NR, EC 70/2012)')
where c.regraaposentadoria = 'INVALIDEZ_ART40_2012';

update configuracaoaposentadoria c set c.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Invalidez (Art. 23, 5º LM 1793/09)')
where c.regraaposentadoria = 'INVALIDEZ_ART23_2009';

update configuracaoaposentadoria c set c.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Compulsória(Art.40, CF, NR, EC 20/1998)')
where c.regraaposentadoria = 'COMPULSORIA';

update configuracaoaposentadoria c set c.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária Integral (Art. 40 CF NR, EC 20/1998, EC 70/2012)')
where c.regraaposentadoria = 'VOLUNTARIA_INTEGRAL';

update configuracaoaposentadoria c set c.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária Por Idade(Art.40, CF, NR, EC 20/1998)')
where c.regraaposentadoria = 'VOLUNTARIA_POR_IDADE';

update configuracaoaposentadoria c set c.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária do Magistério (Art.40, CF, NR, EC 20/1998)')
where c.regraaposentadoria = 'VOLUNTARIA_ESPECIAL_MAGISTERIO';

update configuracaoaposentadoria c set c.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária Proporcional (Art.40 CF)')
where c.regraaposentadoria = 'VOLUNTARIA_PROPORCIONAL';
