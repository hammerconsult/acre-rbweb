update aposentadoria a set a.regraaposentadoria_id = (select max(r.id) from regraaposentadoria r where r.descricao = 'Voluntária Integral Comum (Art. 3º, CF, NR EC 47/2005)')
where a.regraaposentadoria = 'VOLUNTARIA_INTEGRAL_COMNUM_ART_2005';

update aposentadoria a set a.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária Integral Comum(transição - Art.6º, EC 41/2003)')
where a.regraaposentadoria = 'VOLUNTARIA_INTEGRAL_COMNUM_TRANSICAO_ART6_2003';

update aposentadoria a set a.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária Integral Especial(transição - Art.6º, EC 41/2003)')
where a.regraaposentadoria = 'VOLUNTARIA_INTEGRAL_ESPECIAL_MAGISTERIO_ART6_2003';

update aposentadoria a set a.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária Integral Comum(transição - Art.2º, EC 41/2003)')
where a.regraaposentadoria = 'VOLUNTARIA_INTEGRAL_COMUM_TRANSICAO_ART2_2003';

update aposentadoria a set a.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária Integral Especial(transição - Art.2º, EC 41/2003)')
where a.regraaposentadoria = 'VOLUNTARIA_INTEGRAL_ESPECIAL_TRANSICAO_ART2_2003';

update aposentadoria a set a.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Invalidez(Art.40, CF, NR, EC 41/2003)')
where a.regraaposentadoria = 'INVALIDEZ';

update aposentadoria a set a.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Invalidez(Art.40, EC, NR, EC 70/2012)')
where a.regraaposentadoria = 'INVALIDEZ_ART40_2012';

update aposentadoria a set a.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Invalidez (Art. 23, 5º LM 1793/09)')
where a.regraaposentadoria = 'INVALIDEZ_ART23_2009';

update aposentadoria a set a.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Compulsória(Art.40, CF, NR, EC 20/1998)')
where a.regraaposentadoria = 'COMPULSORIA';

update aposentadoria a set a.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária Integral (Art. 40 CF NR, EC 20/1998, EC 70/2012)')
where a.regraaposentadoria = 'VOLUNTARIA_INTEGRAL';

update aposentadoria a set a.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária Por Idade(Art.40, CF, NR, EC 20/1998)')
where a.regraaposentadoria = 'VOLUNTARIA_POR_IDADE';

update aposentadoria a set a.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária do Magistério (Art.40, CF, NR, EC 20/1998)')
where a.regraaposentadoria = 'VOLUNTARIA_ESPECIAL_MAGISTERIO';

update aposentadoria a set a.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária Proporcional (Art.40 CF)')
where a.regraaposentadoria = 'VOLUNTARIA_PROPORCIONAL';

update aposentadoria a set a.regraaposentadoria_id = (select r.id from regraaposentadoria r where r.descricao = 'Voluntária Proporcional (EC nº 103/2019, Art. 4º, §9º)')
where a.regraaposentadoria = 'VOLUNTARIA_PROPORCIONAL_ART4_2019';
