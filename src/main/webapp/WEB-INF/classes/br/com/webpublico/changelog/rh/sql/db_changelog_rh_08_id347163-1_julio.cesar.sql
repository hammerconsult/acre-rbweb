insert into eventofp
(id, codigo, complementoreferencia, descricao, formula, formulavalorintegral, referencia, regra, tipoeventofp,
 automatico, unidadereferencia, tipoexecucaoep, tipobase, valorbasedecalculo, descricaoreduzida, ativo, quantificacao,
 tipolancamentofp, calculoretroativo, verbafixa, naopermitelancamento, tipocalculo13, tipobasefp_id, referenciafp_id,
 estornoferias, ordemprocessamento, consignacao, dataregistro, dataalteracao, tipoprevidenciafp_id, tipodeconsignacao,
 bloqueioferias, arredondarvalor, proporcionalizadiastrab, eventofpagrupador_id, valormaximolancamento,
 identificacaoeventofp, propmesestrabalhados, entidade_id, ultimoacumuladoemdezembro, basefp_id,
 tipolancamentofpsimplificado, controlecargolotacao, naturezarefenciacalculo, verbadeferias, classificacaoverba_id,
 tipoclassificacaoconsignacao, exibirnafichafinanceira, apuracaoir, remuneracaoprincipal, naoenviarverbasicap,
 exibirnaficharpa, bloqueiolicencapremio, tipocontribuicaobbprev, codigocontribuicaobbprev)
values (HIBERNATE_SEQUENCE.nextval, '556', null, 'Grat. Controle Interno - RBPREV',
        'return calculador.salarioBaseServidorReferenciaFG(''CONTROLE_INTERNO'') / 100 * calculador.percentualAcessoFG(''CONTROLE_INTERNO'') / calculador.obterDiasDoMes() * calculador.diasTrabalhadosFGPorTipo(''CONTROLE_INTERNO'');',
        'return calculador.salarioBaseServidorReferenciaFG(''CONTROLE_INTERNO'') / 100 * calculador.percentualAcessoFG(''CONTROLE_INTERNO'');',
        'return calculador.diasTrabalhadosFGPorTipo(''CONTROLE_INTERNO'');',
        'return calculador.temFuncaoGratificada(''CONTROLE_INTERNO'');', 'VANTAGEM', 1, null, 'FOLHA', null,
        'return calculador.salarioBaseServidorReferenciaFG(''CONTROLE_INTERNO'');', 'Grat Contr Interno', 1, 0,
        'REFERENCIA', 1, 0, 0, 'NAO', null, null, 0, 110, 0, timestamp '2025-04-23 09:38:01.000000', null, null, null,
        null, 0, 0, null, 5000, null, 0, null, null, null, null, 0, null, null, 10489910291, null, 0, 0, 1, 0, null,
        null, null, null);



insert into eventofpempregador
(id, entidade_id, iniciovigencia, fimvigencia, eventofp_id, identificacaotabela, naturezarubrica_id,
 incidenciaprevidencia_id, incidenciatributariairrf_id, incidenciatributariafgts_id, incidenciatributariarpps_id,
 verbadeferias, tetoremuneratorio)
values (HIBERNATE_SEQUENCE.nextval, 135799933, timestamp '2023-01-01 00:00:00.000000', null, (select e.id from eventofp e where e.codigo = '556'), 'RBPREV', 803879964,
        803705760, 803705790, 803705835, 803705841, null, 0);


insert into eventofpunidade
(id, eventofp_id, unidadeorganizacional_id)
values(HIBERNATE_SEQUENCE.nextval, (select e.id from eventofp e where e.codigo = '556'), 58758827);


insert into eventofptipofolha
(id, eventofp_id, tipofolhadepagamento)
values (HIBERNATE_SEQUENCE.nextval, (select e.id from eventofp e where e.codigo = '556'), 'NORMAL');
insert into eventofptipofolha
(id, eventofp_id, tipofolhadepagamento)
values (HIBERNATE_SEQUENCE.nextval, (select e.id from eventofp e where e.codigo = '556'), 'COMPLEMENTAR');
insert into eventofptipofolha
(id, eventofp_id, tipofolhadepagamento)
values (HIBERNATE_SEQUENCE.nextval, (select e.id from eventofp e where e.codigo = '556'), 'ADIANTAMENTO_13_SALARIO');
insert into eventofptipofolha
(id, eventofp_id, tipofolhadepagamento)
values (HIBERNATE_SEQUENCE.nextval, (select e.id from eventofp e where e.codigo = '556'), 'SALARIO_13');
insert into eventofptipofolha
(id, eventofp_id, tipofolhadepagamento)
values (HIBERNATE_SEQUENCE.nextval, (select e.id from eventofp e where e.codigo = '556'), 'MANUAL');
