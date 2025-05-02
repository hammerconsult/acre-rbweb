create or replace package body pacote_consulta_de_debitos
as
    function getultimasituacaoparcela(
        param_id_parcela in number)
        return number
    as
        situacao_id number;
begin
select id
into situacao_id
from (select id
      from situacaoparcelavalordivida
      where parcela_id = param_id_parcela
      order by datalancamento desc,
               id desc)
where rownum = 1;
return situacao_id;
end getultimasituacaoparcela;
    function getvalorjuros(
        param_data_operacao in date,
        param_id_parcela in number,
        calcula_desconto in number)
        return number
    as
        tipo_aplic_acrescimo_exception exception;
        configuracao_acrescimos  configuracaoacrescimos%rowtype;
        valor_juros              number;
        multiplicador            number;
        diferenca_dias           number;
        diferenca_meses          number;
        diferenca_anos           number;
        qtdade_dias_mes_anterior number;
        param_situacao           varchar2(50);
        param_tipo_calc          varchar2(50);
        param_divida_id          number;
        param_valor              number;
        param_is_divida_ativa    number;
        param_data_vencimento    date;
        param_exercicio          number;
        cota_unica               number;
        pagto_parcelamento       number;
        juros_original           number;
        isenta_juros             number;
        data_isencao_juros       date;
begin
        param_valor := 0;
execute immediate 'alter session set NLS_DATE_FORMAT = "dd/MM/yyyy"';
select pvd.vencimento,
       vd.divida_id,
       coalesce(pvd.dividaativa, 0),
       spvd.situacaoparcela,
       exercicio.ano,
       coalesce(op.promocional, 0),
       calculo.tipocalculo
into param_data_vencimento,
    param_divida_id,
    param_is_divida_ativa,
    param_situacao,
    param_exercicio,
    cota_unica,
    param_tipo_calc
from parcelavalordivida pvd
         inner join opcaopagamento op
                    on op.id = pvd.opcaopagamento_id
         inner join valordivida vd
                    on vd.id = pvd.valordivida_id
         inner join calculo
                    on calculo.id = vd.calculo_id
         inner join exercicio
                    on exercicio.id = vd.exercicio_id
         inner join situacaoparcelavalordivida spvd
                    on spvd.parcela_id = pvd.id
where spvd.id = getultimasituacao(pvd.id)
  and pvd.id = param_id_parcela;
if cota_unica = 1 and param_data_vencimento >= param_data_operacao then
            return 0;
end if;
        if param_data_vencimento >= to_date(param_data_operacao, 'dd/MM/yyyy') then
            valor_juros := getvalortipotributo(param_id_parcela, 'JUROS');
            if calcula_desconto = 1 then
                pagto_parcelamento := coalesce(pgtoparccancparcelamento(param_id_parcela), 0);
                if pagto_parcelamento >= valor_juros then
                    valor_juros := 0;
else
                    valor_juros := valor_juros - pagto_parcelamento;
end if;
end if;
return valor_juros;
end if;
        if param_situacao = 'PAGO' or param_situacao = 'BAIXADO' then
select sum(idm.juros)
into valor_juros
from dam dm
         inner join itemdam idm
                    on idm.dam_id = dm.id
where idm.id =
      (select max(s_idm.id)
       from itemdam s_idm
                inner join dam s_dam
                           on s_dam.id = s_idm.dam_id
       where s_dam.situacao = 'PAGO'
         and s_idm.parcela_id = param_id_parcela);
return round(valor_juros, 2);
end if;
        if param_situacao = 'PARCELADO' then
select sum(ipp.juros)
into valor_juros
from itemprocessoparcelamento ipp
         inner join processoparcelamento pp
                    on pp.id = ipp.processoparcelamento_id
where ipp.parcelavalordivida_id = param_id_parcela
  and ipp.id =
      (select max(id)
       from itemprocessoparcelamento
       where parcelavalordivida_id = param_id_parcela);
return round(valor_juros, 2);
end if;
        if param_situacao = 'PAGO_PARCELAMENTO' then
select coalesce(sum(pagamento.juros), -1)
into valor_juros
from pagamentopormovtributaria pagamento
where parcela_id = param_id_parcela;
if valor_juros = -1 then
select ipp.juros
into valor_juros
from itemprocessoparcelamento ipp
where ipp.id =
      (select max(id)
       from itemprocessoparcelamento
       where parcelavalordivida_id = param_id_parcela);
end if;
return round(valor_juros, 2);
end if;
        if param_situacao = 'PAGO_SUBVENCAO' then
select sub.juros
into valor_juros
from subvencaoparcela sub
where sub.id =
      (select min(id)
       from subvencaoparcela
       where parcelavalordivida_id = param_id_parcela);
return round(valor_juros, 2);
end if;
        if param_situacao = 'SUBSTITUIDA_POR_COMPENSACAO' then
select pagamento.juros
into valor_juros
from pagamentojudicialparcela pagamento
where pagamento.id =
      (select min(id)
       from pagamentojudicialparcela
       where parcelavalordivida_id = param_id_parcela);
return round(valor_juros, 2);
end if;
        if param_situacao = 'COMPENSACAO' then
select pagamento.juros
into valor_juros
from pagamentojudicialparcela pagamento
where pagamento.id =
      (select min(id)
       from pagamentojudicialparcela
       where parcelavalordivida_id = param_id_parcela);
return round(valor_juros, 2);
end if;
        if param_situacao <> 'EM_ABERTO' then
            return getvalortipotributo(param_id_parcela, 'JUROS');
end if;
        if param_tipo_calc = 'NFSE' then
select coalesce(sum(d.juros), 0)
into valor_juros
from dam d
         inner join itemdam idam on idam.dam_id = d.id
         left join itemlotebaixa ilb on ilb.dam_id = d.id
where idam.parcela_id = param_id_parcela
  and d.situacao = 'ABERTO'
  and d.vencimento >= current_date;
if valor_juros > 0 then
                return valor_juros;
else
                valor_juros := 0;
end if;
end if;
select coalesce(max(pro.isentajuros), 0),
       max(pro.datafim)
into isenta_juros,
    data_isencao_juros
from isencaoacrescimoparcela isp
         inner join processoisencaoacrescimos pro
                    on pro.id = isp.processoisencaoacrescimos_id
where isp.parcela_id = param_id_parcela
  and pro.situacao = 'ATIVO'
  and trunc(param_data_operacao) >= pro.datainicio;
if isenta_juros = 1 and trunc(param_data_operacao) <= data_isencao_juros then
            return 0;
end if;
        if isenta_juros = 1 and trunc(param_data_operacao) > data_isencao_juros then
            param_data_vencimento := data_isencao_juros;
end if;
select ca.*
into configuracao_acrescimos
from dividaacrescimo da
         inner join configuracaoacrescimos ca
                    on ca.id = da.acrescimo_id
where da.divida_id = param_divida_id
  and da.iniciovigencia <= param_data_operacao
  and (da.finalvigencia is null
    or da.finalvigencia >= param_data_operacao);
if configuracao_acrescimos.id is null then
            return getvalortipotributo(param_id_parcela, 'JUROS');
end if;
        if contemincidencia('IMPOSTO', 'JUROS', configuracao_acrescimos.id) = 1 then
            param_valor := param_valor + getvalortipotributo(param_id_parcela, 'IMPOSTO');
end if;
        if contemincidencia('TAXA', 'JUROS', configuracao_acrescimos.id) = 1 then
            param_valor := param_valor + getvalortipotributo(param_id_parcela, 'TAXA');
end if;
        if contemincidencia('CORRECAO', 'JUROS', configuracao_acrescimos.id) = 1 then
            param_valor := param_valor + getvalorcorrecao(param_id_parcela, extract(year from param_data_vencimento), 0,
                                                          param_data_operacao);
end if;
        if contemincidencia('MULTA_ORIGINAL', 'JUROS', configuracao_acrescimos.id) = 1 then
            param_valor := param_valor + getvalortipotributo(param_id_parcela, 'MULTA');
end if;
        if contemincidencia('COREECAO_ORIGINAL', 'JUROS', configuracao_acrescimos.id) = 1 then
            param_valor := param_valor + getvalortipotributo(param_id_parcela, 'CORRECAO');
end if;
        if contemincidencia('HONORARIOS_ORIGINAL', 'JUROS', configuracao_acrescimos.id) = 1 then
            param_valor := param_valor + getvalortipotributo(param_id_parcela, 'HONORARIOS');
end if;
        if contemincidencia('JUROS_ORIGINAL', 'JUROS', configuracao_acrescimos.id) = 1 then
            param_valor := param_valor + getvalortipotributo(param_id_parcela, 'JUROS');
end if;
        diferenca_dias := trunc(param_data_operacao) - trunc(param_data_vencimento);
        diferenca_meses := diferenca_dias / 30;
        if diferenca_meses < 0 then
            diferenca_meses := diferenca_meses + 12;
end if;
select to_number(to_char(last_day(param_data_operacao), 'dd'))
into qtdade_dias_mes_anterior
from dual;
if diferenca_dias < 0 then
            diferenca_dias := diferenca_dias + qtdade_dias_mes_anterior;
            diferenca_meses := diferenca_meses - 1;
end if;
        if configuracao_acrescimos.jurosfracionado = 1 then
            multiplicador := diferenca_meses + (diferenca_dias / qtdade_dias_mes_anterior);
else
            if diferenca_dias > 0 then
                if round(diferenca_meses, 0) < diferenca_meses then
                    diferenca_meses := diferenca_meses + 1;
end if;
                multiplicador := round(diferenca_meses, 0);
end if;
end if;
        multiplicador := multiplicador / 100;
        if param_is_divida_ativa = 1 then
            valor_juros := (param_valor * multiplicador) * configuracao_acrescimos.valorjurosdividaativa;
else
            valor_juros := (param_valor * multiplicador) * configuracao_acrescimos.valorjurosexercicio;
end if;
        juros_original := getvalortipotributo(param_id_parcela, 'JUROS');
        valor_juros := round(valor_juros + juros_original, 2);
        if calcula_desconto = 1 then
            pagto_parcelamento := coalesce(pgtoparccancparcelamento(param_id_parcela), 0);
            if pagto_parcelamento >= valor_juros then
                valor_juros := 0;
else
                valor_juros := valor_juros - pagto_parcelamento;
end if;
end if;
return valor_juros;
exception
        when no_data_found then
            return 0;
when tipo_aplic_acrescimo_exception then
            raise_application_error(-20000, 'TIPO DE APLICAÇÃO DE ACRÉSCIMO INVÁLIDO!');
end getvalorjuros;
    function getvalormulta(
        param_data_operacao in date,
        param_id_parcela in number,
        calcula_desconto in number)
        return number
    as
        tipo_aplic_acrescimo_exception exception;
        configuracao_acrescimos configuracaoacrescimos%rowtype;
        item_configuracao_valor number;
        qtdade_dias_atraso      number;
        percentual_multa        number;
        valor_multa             number;
        param_situacao          varchar2(50);
        param_tipo_calc         varchar2(50);
        param_divida_id         number;
        param_valor             number;
        param_is_divida_ativa   number;
        param_data_vencimento   date;
        param_exercicio         number;
        cota_unica              number;
        juros                   number;
        pagto_parcelamento      number;
        multa_original          number;
        isenta_multa            number;
begin
execute immediate 'alter session set NLS_DATE_FORMAT = "dd/MM/yyyy"';
param_valor := 0;
        percentual_multa := 0;
        qtdade_dias_atraso := 0;
select pvd.vencimento,
       vd.divida_id,
       coalesce(pvd.dividaativa, 0),
       spvd.situacaoparcela,
       exercicio.ano,
       coalesce(op.promocional, 0),
       calculo.tipocalculo
into param_data_vencimento,
    param_divida_id,
    param_is_divida_ativa,
    param_situacao,
    param_exercicio,
    cota_unica,
    param_tipo_calc
from parcelavalordivida pvd
         inner join opcaopagamento op
                    on op.id = pvd.opcaopagamento_id
         inner join valordivida vd
                    on vd.id = pvd.valordivida_id
         inner join calculo
                    on calculo.id = vd.calculo_id
         inner join exercicio
                    on exercicio.id = vd.exercicio_id
         inner join situacaoparcelavalordivida spvd
                    on spvd.parcela_id = pvd.id
where spvd.id = getultimasituacao(pvd.id)
  and pvd.id = param_id_parcela;
if cota_unica = 1 and param_data_vencimento >= param_data_operacao then
            return 0;
end if;
        if param_data_vencimento >= to_date(param_data_operacao, 'dd/MM/yyyy') then
            -----------------------------------------------------------------------------------------
            valor_multa := getvalortipotributo(param_id_parcela, 'MULTA');
            if calcula_desconto = 1 then
                pagto_parcelamento := pgtoparccancparcelamento(param_id_parcela);
                juros := getvalorjuros(param_data_operacao, param_id_parcela, 0);
                if pagto_parcelamento >= juros then
                    pagto_parcelamento := pagto_parcelamento - juros;
                    if pagto_parcelamento >= valor_multa then
                        valor_multa := 0;
else
                        valor_multa := valor_multa - pagto_parcelamento;
end if;
end if;
end if;
return valor_multa;
-----------------------------------------------------------------------------------------
end if;
        if param_situacao = 'PAGO' or param_situacao = 'BAIXADO' then
select sum(idm.multa)
into valor_multa
from dam dm
         inner join itemdam idm
                    on idm.dam_id = dm.id
where idm.id =
      (select max(s_idm.id)
       from itemdam s_idm
                inner join dam s_dam
                           on s_dam.id = s_idm.dam_id
       where s_dam.situacao = 'PAGO'
         and s_idm.parcela_id = param_id_parcela);
return round(valor_multa, 2);
end if;
        if param_situacao = 'PAGO_PARCELAMENTO' then
select coalesce(sum(pagamento.multa), -1)
into valor_multa
from pagamentopormovtributaria pagamento
where parcela_id = param_id_parcela;
if valor_multa = -1 then
select ipp.multa
into valor_multa
from itemprocessoparcelamento ipp
where ipp.id =
      (select max(id)
       from itemprocessoparcelamento
       where parcelavalordivida_id = param_id_parcela);
return round(valor_multa, 2);
end if;
return round(valor_multa, 2);
end if;
        if param_situacao = 'PARCELADO' then
select sum(ipp.multa)
into valor_multa
from itemprocessoparcelamento ipp
         inner join processoparcelamento pp
                    on pp.id = ipp.processoparcelamento_id
where ipp.parcelavalordivida_id = param_id_parcela
  and ipp.id =
      (select max(id)
       from itemprocessoparcelamento
       where parcelavalordivida_id = param_id_parcela);
return round(valor_multa, 2);
end if;
        if param_situacao = 'PAGO_SUBVENCAO' then
select sub.multa
into valor_multa
from subvencaoparcela sub
where sub.id =
      (select min(id)
       from subvencaoparcela
       where parcelavalordivida_id = param_id_parcela);
return round(valor_multa, 2);
end if;
        if param_situacao = 'SUBSTITUIDA_POR_COMPENSACAO' then
select pagamento.multa
into valor_multa
from pagamentojudicialparcela pagamento
where pagamento.id =
      (select min(id)
       from pagamentojudicialparcela
       where parcelavalordivida_id = param_id_parcela);
return round(valor_multa, 2);
end if;
        if param_situacao = 'COMPENSACAO' then
select pagamento.multa
into valor_multa
from pagamentojudicialparcela pagamento
where pagamento.id =
      (select min(id)
       from pagamentojudicialparcela
       where parcelavalordivida_id = param_id_parcela);
return round(valor_multa, 2);
end if;
        if param_situacao <> 'EM_ABERTO' then
            return getvalortipotributo(param_id_parcela, 'MULTA');
end if;
        if param_tipo_calc = 'NFSE' then
select coalesce(sum(d.multa), 0)
into valor_multa
from dam d
         inner join itemdam idam on idam.dam_id = d.id
         left join itemlotebaixa ilb on ilb.dam_id = d.id
where idam.parcela_id = param_id_parcela
  and d.situacao = 'ABERTO'
  and d.vencimento >= current_date;
if valor_multa > 0 then
                return valor_multa;
else
                valor_multa := 0;
end if;
end if;
select coalesce(max(pro.isentamulta), 0)
into isenta_multa
from isencaoacrescimoparcela isp
         inner join processoisencaoacrescimos pro
                    on pro.id = isp.processoisencaoacrescimos_id
where isp.parcela_id = param_id_parcela
  and pro.situacao = 'ATIVO'
  and trunc(param_data_operacao) between pro.datainicio and coalesce(datafim, trunc(param_data_operacao));
if isenta_multa = 1 then
            return 0;
end if;
select ca.*
into configuracao_acrescimos
from dividaacrescimo da
         inner join configuracaoacrescimos ca
                    on ca.id = da.acrescimo_id
where da.divida_id = param_divida_id
  and da.iniciovigencia <= param_data_operacao
  and (da.finalvigencia is null
    or da.finalvigencia >= param_data_operacao);
if configuracao_acrescimos.id is null then
            return 0;
end if;
        if contemincidencia('IMPOSTO', 'MULTA', configuracao_acrescimos.id) = 1 then
            param_valor := param_valor + getvalortipotributo(param_id_parcela, 'IMPOSTO');
end if;
        if contemincidencia('TAXA', 'MULTA', configuracao_acrescimos.id) = 1 then
            param_valor := param_valor + getvalortipotributo(param_id_parcela, 'TAXA');
end if;
        if contemincidencia('CORRECAO', 'MULTA', configuracao_acrescimos.id) = 1 then
            param_valor := param_valor + getvalorcorrecao(param_id_parcela, extract(year from param_data_vencimento), 0,
                                                          param_data_operacao);
end if;
        if contemincidencia('MULTA_ORIGINAL', 'MULTA', configuracao_acrescimos.id) = 1 then
            param_valor := param_valor + getvalortipotributo(param_id_parcela, 'MULTA');
end if;
        if contemincidencia('COREECAO_ORIGINAL', 'MULTA', configuracao_acrescimos.id) = 1 then
            param_valor := param_valor + getvalortipotributo(param_id_parcela, 'CORRECAO');
end if;
        if contemincidencia('HONORARIOS_ORIGINAL', 'MULTA', configuracao_acrescimos.id) = 1 then
            param_valor := param_valor + getvalortipotributo(param_id_parcela, 'HONORARIOS');
end if;
        if contemincidencia('JUROS_ORIGINAL', 'MULTA', configuracao_acrescimos.id) = 1 then
            param_valor := param_valor + getvalortipotributo(param_id_parcela, 'JUROS');
end if;
        qtdade_dias_atraso := round(param_data_operacao - param_data_vencimento);
        if param_is_divida_ativa = 0 then
select valor
into item_configuracao_valor
from itemconfiguracaoacrescimos
where qntdias >= qtdade_dias_atraso
  and configuracaoacrescimos_id = configuracao_acrescimos.id
  and rownum = 1
order by qntdias;
if item_configuracao_valor is not null then
                percentual_multa := item_configuracao_valor / 100;
end if;
else
            percentual_multa := configuracao_acrescimos.valordividaativamulta / 100;
end if;
        valor_multa := param_valor * percentual_multa;
        -- VALOR_MULTA      := ROUND(VALOR_MULTA + MULTA_ORIGINAL, 2);
        if calcula_desconto = 1 then
            pagto_parcelamento := pgtoparccancparcelamento(param_id_parcela);
            juros := getvalorjuros(param_data_operacao, param_id_parcela, 0);
            if pagto_parcelamento >= juros then
                pagto_parcelamento := pagto_parcelamento - juros;
                if pagto_parcelamento >= valor_multa then
                    valor_multa := 0;
else
                    valor_multa := valor_multa - pagto_parcelamento;
end if;
end if;
end if;
return valor_multa;
exception
        when no_data_found then
            return 0;
when tipo_aplic_acrescimo_exception then
            raise_application_error(-20000, 'TIPO DE APLICAÇÃO DE ACRÉSCIMO INVÁLIDO!');
end getvalormulta;
    function getvalorcorrecao(
        param_id_parcela in number,
        param_exercicio_ano in number,
        calcula_desconto in number,
        param_data_operacao in date)
        return number
        is
        valorcorrigido              number;
        param_divida_id             number;
        param_saldo                 number;
        param_data_registro_parcela date;
        param_situacao              varchar2(50);
        param_tipo_calc             varchar2(50);
        param_vencimento            date;
        cota_unica                  number;
        juros                       number;
        multa                       number;
        pagto_parcelamento          number;
        isenta_correcao             number;
        ufm_epoca                   number;
        ufm_atual                   number;
        data_isencao_correcao       date;
        configuracao_acrescimos     configuracaoacrescimos%rowtype;
begin
execute immediate 'alter session set NLS_DATE_FORMAT = "dd/MM/yyyy"';
param_saldo := 0;
select pvd.dataregistro,
       spvd.situacaoparcela,
       divida.id,
       pvd.vencimento,
       coalesce(op.promocional, 0),
       calculo.tipocalculo
into param_data_registro_parcela,
    param_situacao,
    param_divida_id,
    param_vencimento,
    cota_unica,
    param_tipo_calc
from parcelavalordivida pvd
         inner join opcaopagamento op
                    on op.id = pvd.opcaopagamento_id
         inner join valordivida vd
                    on vd.id = pvd.valordivida_id
         inner join calculo
                    on calculo.id = vd.calculo_id
         inner join exercicio
                    on exercicio.id = vd.exercicio_id
         inner join divida
                    on divida.id = vd.divida_id
         inner join situacaoparcelavalordivida spvd
                    on spvd.parcela_id = pvd.id
where spvd.id = getultimasituacao(pvd.id)
  and pvd.id = param_id_parcela;
if cota_unica = 1 and param_data_operacao < param_vencimento then
            return 0;
end if;
        if param_data_operacao < param_vencimento then
            ----------------------------------------------------------------------------------------------
            valorcorrigido := getvalortipotributo(param_id_parcela, 'CORRECAO');
            if calcula_desconto = 1 then
                valorcorrigido := getvalortipotributo(param_id_parcela, 'CORRECAO');
                pagto_parcelamento := pgtoparccancparcelamento(param_id_parcela);
                juros := getvalorjuros(param_data_operacao, param_id_parcela, 0);
                multa := getvalormulta(param_data_operacao, param_id_parcela, 0);
                if pagto_parcelamento >= (juros + multa) then
                    pagto_parcelamento := pagto_parcelamento - (juros + multa);
                    if pagto_parcelamento >= valorcorrigido then
                        valorcorrigido := 0;
else
                        valorcorrigido := valorcorrigido - pagto_parcelamento;
end if;
end if;
end if;
return valorcorrigido;
end if;
        ----------------------------------------------------------------------------------------------
        --  RETURN GETVALORTIPOTRIBUTO(PARAM_ID_PARCELA, 'CORRECAO');
        if param_situacao = 'PAGO' or param_situacao = 'BAIXADO' then
select sum(idm.correcaomonetaria)
into valorcorrigido
from dam dm
         inner join itemdam idm
                    on idm.dam_id = dm.id
where idm.id =
      (select max(s_idm.id)
       from itemdam s_idm
                inner join dam s_dam
                           on s_dam.id = s_idm.dam_id
       where s_dam.situacao = 'PAGO'
         and s_idm.parcela_id = param_id_parcela);
return round(valorcorrigido, 2);
end if;
        if param_situacao = 'PAGO_PARCELAMENTO' then
select coalesce(sum(pagamento.correcao), -1)
into valorcorrigido
from pagamentopormovtributaria pagamento
where parcela_id = param_id_parcela;
if valorcorrigido = -1 then
select ipp.correcao
into valorcorrigido
from itemprocessoparcelamento ipp
where ipp.id =
      (select max(id)
       from itemprocessoparcelamento
       where parcelavalordivida_id = param_id_parcela);
end if;
return round(valorcorrigido, 2);
end if;
        if param_situacao = 'PARCELADO' then
select sum(ipp.correcao)
into valorcorrigido
from itemprocessoparcelamento ipp
         inner join processoparcelamento pp
                    on pp.id = ipp.processoparcelamento_id
where ipp.parcelavalordivida_id = param_id_parcela
  and ipp.id =
      (select max(id)
       from itemprocessoparcelamento
       where parcelavalordivida_id = param_id_parcela);
return round(valorcorrigido, 2);
end if;
        if param_situacao = 'PAGO_SUBVENCAO' then
select sub.correcao
into valorcorrigido
from subvencaoparcela sub
where sub.id =
      (select min(id)
       from subvencaoparcela
       where parcelavalordivida_id = param_id_parcela);
return round(valorcorrigido, 2);
end if;
        if param_situacao = 'SUBSTITUIDA_POR_COMPENSACAO' then
select pagamento.correcao
into valorcorrigido
from pagamentojudicialparcela pagamento
where pagamento.id =
      (select min(id)
       from pagamentojudicialparcela
       where parcelavalordivida_id = param_id_parcela);
return round(valorcorrigido, 2);
end if;
        if param_situacao = 'COMPENSACAO' then
select pagamento.correcao
into valorcorrigido
from pagamentojudicialparcela pagamento
where pagamento.id =
      (select min(id)
       from pagamentojudicialparcela
       where parcelavalordivida_id = param_id_parcela);
return round(valorcorrigido, 2);
end if;
        if param_situacao <> 'EM_ABERTO' then
            return getvalortipotributo(param_id_parcela, 'CORRECAO');
end if;
        if param_tipo_calc = 'NFSE' then
select coalesce(sum(d.correcaomonetaria), 0)
into valorcorrigido
from dam d
         inner join itemdam idam on idam.dam_id = d.id
         left join itemlotebaixa ilb on ilb.dam_id = d.id
where idam.parcela_id = param_id_parcela
  and d.situacao = 'ABERTO'
  and d.vencimento >= current_date;
if valorcorrigido > 0 then
                return valorcorrigido;
else
                valorcorrigido := 0;
end if;
end if;
select coalesce(max(pro.isentacorrecao), 0),
       max(pro.datafim)
into isenta_correcao,
    data_isencao_correcao
from isencaoacrescimoparcela isp
         inner join processoisencaoacrescimos pro
                    on pro.id = isp.processoisencaoacrescimos_id
where isp.parcela_id = param_id_parcela
  and pro.situacao = 'ATIVO'
  and trunc(param_data_operacao) >= pro.datainicio;
if isenta_correcao = 1 and trunc(param_data_operacao) <= data_isencao_correcao then
            return 0;
end if;
        if isenta_correcao = 1 and trunc(param_data_operacao) > data_isencao_correcao then
            param_vencimento := data_isencao_correcao;
end if;
select ca.*
into configuracao_acrescimos
from dividaacrescimo da
         inner join configuracaoacrescimos ca
                    on ca.id = da.acrescimo_id
where da.divida_id = param_divida_id
  and da.iniciovigencia <= param_data_operacao
  and (da.finalvigencia is null
    or da.finalvigencia >= param_data_operacao);
if configuracao_acrescimos.id = null then
            return 0;
end if;
        if configuracao_acrescimos.calculacorrecao <> 1 then
            return 0;
end if;
        if contemincidencia('IMPOSTO', 'CORRECAO', configuracao_acrescimos.id) = 1 then
            param_saldo := param_saldo + getvalortipotributo(param_id_parcela, 'IMPOSTO');
end if;
        if contemincidencia('TAXA', 'CORRECAO', configuracao_acrescimos.id) = 1 then
            param_saldo := param_saldo + getvalortipotributo(param_id_parcela, 'TAXA');
end if;
        if contemincidencia('MULTA_ORIGINAL', 'CORRECAO', configuracao_acrescimos.id) = 1 then
            param_saldo := param_saldo + getvalortipotributo(param_id_parcela, 'MULTA');
end if;
        if contemincidencia('COREECAO_ORIGINAL', 'CORRECAO', configuracao_acrescimos.id) = 1 then
            param_saldo := param_saldo + getvalortipotributo(param_id_parcela, 'CORRECAO');
end if;
        if contemincidencia('HONORARIOS_ORIGINAL', 'CORRECAO', configuracao_acrescimos.id) = 1 then
            param_saldo := param_saldo + getvalortipotributo(param_id_parcela, 'HONORARIOS');
end if;
        if contemincidencia('JUROS_ORIGINAL', 'CORRECAO', configuracao_acrescimos.id) = 1 then
            param_saldo := param_saldo + getvalortipotributo(param_id_parcela, 'JUROS');
end if;

select coalesce(moedadataparcela.valor, 0)
into ufm_epoca
from moeda moedadataparcela
         inner join indiceeconomico indicedataparcela
                    on indicedataparcela.id = moedadataparcela.indiceeconomico_id
where moedadataparcela.mes = to_number(to_char(param_data_registro_parcela, 'MM'))
  and moedadataparcela.ano = param_exercicio_ano
  and indicedataparcela.descricao = 'UFM';
select coalesce(moedaatual.valor, 0)
into ufm_atual
from moeda moedaatual
         inner join indiceeconomico indiceatual
                    on indiceatual.id = moedaatual.indiceeconomico_id
where moedaatual.mes = to_number(to_char(param_data_operacao, 'MM'))
  and moedaatual.ano = to_number(to_char(param_data_operacao, 'YYYY'))
  and indiceatual.descricao = 'UFM';
if param_saldo <= 0 or ufm_atual <= 0 or ufm_epoca <= 0 then
            valorcorrigido := param_saldo + getvalortipotributo(param_id_parcela, 'CORRECAO');
else
            valorcorrigido := (param_saldo / ufm_epoca) * ufm_atual;
            valorcorrigido :=
                    round((valorcorrigido - param_saldo) + getvalortipotributo(param_id_parcela, 'CORRECAO'), 2);
end if;

        if calcula_desconto = 1 then
            pagto_parcelamento := pgtoparccancparcelamento(param_id_parcela);
            juros := getvalorjuros(param_data_operacao, param_id_parcela, 0);
            multa := getvalormulta(param_data_operacao, param_id_parcela, 0);
            if pagto_parcelamento >= (juros + multa) then
                pagto_parcelamento := pagto_parcelamento - (juros + multa);
                if pagto_parcelamento >= valorcorrigido then
                    valorcorrigido := 0;
else
                    valorcorrigido := valorcorrigido - pagto_parcelamento;
end if;
end if;
end if;
return valorcorrigido;
end getvalorcorrecao;
    function getvalordesconto(
        param_id_parcela in number,
        param_data_operacao in date)
        return number
    as
        desconto_percentual number;
        desconto_valor      number;
        situacao_parcela    varchar2(75);
begin
select spvd.situacaoparcela
into situacao_parcela
from situacaoparcelavalordivida spvd
where spvd.id = getultimasituacao(param_id_parcela);
if situacao_parcela = 'PAGO_PARCELAMENTO' or situacao_parcela = 'PARCELADO' then
select sum(pgto.valor)
into desconto_valor
from movimentovalorparcelamento pgto
where pgto.parcela_id = param_id_parcela
  and pgto.parcelamento_id =
      (select max(parcelamento_id)
       from movimentovalorparcelamento
       where parcela_id = param_id_parcela)
  and pgto.tipo = 'DESCONTO';
return desconto_valor;
end if;
        if situacao_parcela = 'PAGO' or situacao_parcela = 'BAIXADO' then
select sum(idm.desconto)
into desconto_valor
from dam dm
         inner join itemdam idm
                    on idm.dam_id = dm.id
where idm.id =
      (select max(s_idm.id)
       from itemdam s_idm
                inner join dam s_dam
                           on s_dam.id = s_idm.dam_id
       where s_dam.situacao = 'PAGO'
         and s_idm.parcela_id = param_id_parcela);
return round(desconto_valor, 2);
end if;
select coalesce(sum(item.valor * (desconto.desconto / 100)), 0)
into desconto_percentual
from descontoitemparcela desconto
         inner join itemparcelavalordivida item
                    on item.id = desconto.itemparcelavalordivida_id
where item.parcelavalordivida_id = param_id_parcela
  and desconto.desconto > 0
  and desconto.tipo = 'PERCENTUAL'
  and trunc(desconto.inicio) <= trunc(param_data_operacao)
  and trunc(coalesce(desconto.fim, param_data_operacao)) >= trunc(param_data_operacao);
select coalesce(sum(desconto.desconto), 0)
into desconto_valor
from descontoitemparcela desconto
         inner join itemparcelavalordivida item
                    on item.id = desconto.itemparcelavalordivida_id
where item.parcelavalordivida_id = param_id_parcela
  and desconto.tipo = 'VALOR'
  and desconto.desconto > 0
  and trunc(desconto.inicio) <= trunc(param_data_operacao)
  and trunc(coalesce(desconto.fim, param_data_operacao)) >= trunc(param_data_operacao);
return round(desconto_percentual + desconto_valor, 2);
end getvalordesconto;
    function getvalortipotributo(
        param_id_parcela in number,
        param_tipo_tributo in varchar2)
        return number
    as
        valor_original number;
        valor_saldo    number;
        valor_soma     number;
begin
select coalesce(pvd.valor, 0)    as parcelavalor,
       coalesce(spvd.saldo, 0)   as itemsaldo,
       coalesce(sum(i.valor), 0) as itemvalor
into valor_original,
    valor_saldo,
    valor_soma
from itemparcelavalordivida i
         inner join parcelavalordivida pvd
                    on i.parcelavalordivida_id = pvd.id
         inner join itemvalordivida iv
                    on iv.id = i.itemvalordivida_id
         inner join tributo t
                    on t.id = iv.tributo_id
         inner join situacaoparcelavalordivida spvd
                    on spvd.parcela_id = pvd.id
where spvd.id = getultimasituacao(pvd.id)
  and pvd.id = param_id_parcela
  and t.tipotributo = param_tipo_tributo
group by pvd.valor,
         spvd.saldo;
if valor_saldo <= 0 then
            valor_saldo := valor_original;
end if;
return round(valor_saldo * ((valor_soma * 100) / valor_original) / 100, 2);
exception
        when no_data_found then
            return 0;
when zero_divide then
            return 0;
end getvalortipotributo;
    function getnumeroparcela(
        param_id_valor_divida in number,
        param_id_opcao_pagamento in number,
        param_promocional in number,
        param_sequencia_parcela in varchar2)
        return varchar2
    as
        numero_parcela varchar2(30);
begin
select case
           when param_promocional = 1
               then (coalesce(param_sequencia_parcela, '')
               || '/1 UN')
           else (coalesce(param_sequencia_parcela, '')
               || '/'
               || to_char(
                     (select coalesce(p.quantidadeparcela, count(p.id))
                      from parcelavalordivida p
                               inner join valordivida v
                                          on v.id = p.valordivida_id
                      where v.id = param_id_valor_divida
                        and p.opcaopagamento_id = param_id_opcao_pagamento
                      group by p.quantidadeparcela)))
           end
into numero_parcela
from dual;
return numero_parcela;
end getnumeroparcela;
    function getvalorhonorarios(
        param_id_parcela in number,
        param_data_operacao in date)
        return number
    as
        tipo_aplic_acrescimo_exception exception;
        configuracao_acrescimos  configuracaoacrescimos%rowtype;
        valor_honorario          number;
        porcentagem_aplicar      number;
        param_valor              number;
        param_divida_id          number;
        param_is_divida_ajuizada number;
        param_situacao           varchar2(50);
        param_situacao_id        number;
        valor_embutido           number;
        param_parcela_vencimento date;
begin
        param_situacao_id := getultimasituacaoparcela(param_id_parcela);
select coalesce(pvd.dividaativaajuizada, 0),
       vd.divida_id,
       spvd.situacaoparcela,
       pvd.vencimento
into param_is_divida_ajuizada,
    param_divida_id,
    param_situacao,
    param_parcela_vencimento
from parcelavalordivida pvd
         inner join valordivida vd
                    on vd.id = pvd.valordivida_id
         inner join situacaoparcelavalordivida spvd
                    on spvd.id = param_situacao_id
where pvd.id = param_id_parcela;
if param_situacao = 'PAGO' or param_situacao = 'BAIXADO' then
select sum(idm.honorarios)
into valor_honorario
from dam dm
         inner join itemdam idm
                    on idm.dam_id = dm.id
where idm.id =
      (select max(s_idm.id)
       from itemdam s_idm
                inner join dam s_dam
                           on s_dam.id = s_idm.dam_id
       where s_dam.situacao = 'PAGO'
         and s_idm.parcela_id = param_id_parcela);
return round(valor_honorario, 2);
end if;
        if param_situacao = 'PAGO_SUBVENCAO' then
select sub.valorhonorarios
into valor_honorario
from subvencaoparcela sub
where sub.id =
      (select min(id)
       from subvencaoparcela
       where parcelavalordivida_id = param_id_parcela);
return round(valor_honorario, 2);
end if;
        if param_situacao = 'SUBSTITUIDA_POR_COMPENSACAO' then
select pagamento.valorhonorarios
into valor_honorario
from pagamentojudicialparcela pagamento
where pagamento.id =
      (select min(id)
       from pagamentojudicialparcela
       where parcelavalordivida_id = param_id_parcela);
return round(valor_honorario, 2);
end if;
        if param_situacao = 'COMPENSACAO' then
select pagamento.valorhonorarios
into valor_honorario
from pagamentojudicialparcela pagamento
where pagamento.id =
      (select min(id)
       from pagamentojudicialparcela
       where parcelavalordivida_id = param_id_parcela);
return round(valor_honorario, 2);
end if;
        if param_situacao = 'PARCELADO' then
select sum(ipp.honorarios)
into valor_honorario
from itemprocessoparcelamento ipp
         inner join processoparcelamento pp
                    on pp.id = ipp.processoparcelamento_id
where ipp.parcelavalordivida_id = param_id_parcela
  and ipp.id =
      (select max(id)
       from itemprocessoparcelamento
       where parcelavalordivida_id = param_id_parcela);
return round(valor_honorario, 2);
end if;
        if param_situacao = 'PAGO_PARCELAMENTO' then
select coalesce(sum(pagamento.honorarios), -1)
into valor_honorario
from pagamentopormovtributaria pagamento
where parcela_id = param_id_parcela;
if valor_honorario = -1 then
select ipp.honorarios
into valor_honorario
from itemprocessoparcelamento ipp
where ipp.id =
      (select max(id)
       from itemprocessoparcelamento
       where parcelavalordivida_id = param_id_parcela);
end if;
return round(valor_honorario, 2);
end if;
        if param_situacao <> 'EM_ABERTO' then
            return getvalortipotributo(param_id_parcela, 'HONORARIOS');
end if;
select ca.*
into configuracao_acrescimos
from dividaacrescimo da
         inner join configuracaoacrescimos ca
                    on ca.id = da.acrescimo_id
where da.divida_id = param_divida_id
  and da.iniciovigencia <= param_data_operacao
  and (da.finalvigencia is null
    or da.finalvigencia >= param_data_operacao);
if configuracao_acrescimos.id = null then
            return 0;
end if;
        if param_is_divida_ajuizada <> 1 then
            valor_honorario := 0;
else
            param_valor := round(getvalorimposto(param_id_parcela, 1, param_data_operacao), 2) +
                           round(getvalortaxa(param_id_parcela, 1, param_data_operacao), 2);
            param_valor := param_valor + round(getvalorjuros(param_data_operacao, param_id_parcela, 1), 2) +
                           round(getvalormulta(param_data_operacao, param_id_parcela, 1), 2);
            param_valor := param_valor + round(
                    getvalorcorrecao(param_id_parcela, extract(year from param_parcela_vencimento), 1,
                                     param_data_operacao), 2);
            param_valor := param_valor - coalesce(getvalordesconto(param_id_parcela, param_data_operacao), 0);
            porcentagem_aplicar := configuracao_acrescimos.honorariosadvocaticios;
            valor_honorario := (porcentagem_aplicar * param_valor) / 100;
end if;
        valor_embutido := getvalortipotributo(param_id_parcela, 'HONORARIOS');
        if valor_embutido > valor_honorario then
            return valor_embutido;
else
            return round(valor_honorario, 2);
end if;
end getvalorhonorarios;
    function getvalorparcelapago(
        param_id_parcela in number,
        param_situacao_parcela in varchar2)
        return number
    as
        valor number;
begin
        if param_situacao_parcela != 'PAGO' and param_situacao_parcela != 'BAIXADO' and
           param_situacao_parcela != 'PAGO_REFIS' and param_situacao_parcela != 'PAGO_PARCELAMENTO' and
           param_situacao_parcela != 'PAGO_SUBVENCAO' and param_situacao_parcela != 'SUBSTITUIDA_POR_COMPENSACAO' and
           param_situacao_parcela != 'COMPENSACAO' then
            return 0;
end if;
        if param_situacao_parcela = 'PAGO_SUBVENCAO' then
select sub.total
into valor
from subvencaoparcela sub
where sub.id =
      (select min(id)
       from subvencaoparcela
       where parcelavalordivida_id = param_id_parcela);
return valor;
end if;
        if param_situacao_parcela = 'SUBSTITUIDA_POR_COMPENSACAO' then
select pagamento.valorcompensado
into valor
from pagamentojudicialparcela pagamento
where pagamento.id =
      (select min(id)
       from pagamentojudicialparcela
       where parcelavalordivida_id = param_id_parcela);
return valor;
end if;
        if param_situacao_parcela = 'COMPENSACAO' then
select pagamento.valorcompensado
into valor
from pagamentojudicialparcela pagamento
where pagamento.id =
      (select min(id)
       from pagamentojudicialparcela
       where parcelavalordivida_id = param_id_parcela);
return valor;
end if;
        if param_situacao_parcela = 'PAGO_PARCELAMENTO' then
select coalesce(sum(pgto.valor), 0)
into valor
from movimentovalorparcelamento pgto
where pgto.parcela_id = param_id_parcela
  and pgto.parcelamento_id =
      (select max(parcelamento_id)
       from movimentovalorparcelamento
       where parcela_id = param_id_parcela)
  and pgto.tipo = 'PAGAMENTO';
if valor = 0 then
select coalesce(sum(pagamento.imposto), 0) + coalesce(sum(pagamento.taxa), 0) +
       coalesce(sum(pagamento.juros), 0) + coalesce(sum(pagamento.multa), 0) +
       coalesce(sum(pagamento.correcao), 0) + coalesce(sum(pagamento.honorarios), 0)
into valor
from pagamentopormovtributaria pagamento
where parcela_id = param_id_parcela;
end if;
return valor;
end if;
select coalesce(sum(it.valorpago), sum(itemlotebaixa.valorpago), 0)
into valor
from itemlotebaixa
         inner join lotebaixa
                    on lotebaixa.id = itemlotebaixa.lotebaixa_id
         inner join dam
                    on dam.id = itemlotebaixa.dam_id
         inner join itemdam
                    on itemdam.dam_id = dam.id
         left join itemlotebaixaparcela it
                   on itemlotebaixa.id = it.itemlotebaixa_id
                       and it.itemdam_id = itemdam.id
where (lotebaixa.situacaolotebaixa = 'BAIXADO'
    or lotebaixa.situacaolotebaixa = 'BAIXADO_INCONSITENTE')
  and itemdam.parcela_id = param_id_parcela;
if (valor is null or valor = 0) then
select (sum(idm.juros) + sum(idm.multa) + sum(idm.correcaomonetaria) + sum(idm.honorarios) +
        sum(idm.valororiginaldevido)) - sum(idm.desconto)
into valor
from dam dm
         inner join itemdam idm
                    on idm.dam_id = dm.id
where idm.id =
      (select max(s_idm.id)
       from itemdam s_idm
                inner join dam s_dam
                           on s_dam.id = s_idm.dam_id
       where s_dam.situacao = 'PAGO'
         and s_idm.parcela_id = param_id_parcela);
end if;
        if (valor is null or valor = 0) then
select sum(atr.valorpago)
into valor
from pagamentoavulso atr
where atr.parcelavalordivida_id = param_id_parcela;
end if;
        if (valor is null or valor = 0) then
select max(pix.valorpago)
into valor
from pix
         inner join dam on pix.id = dam.pix_id
         inner join itemdam on dam.id = itemdam.dam_id
         inner join parcelavalordivida pvd on itemdam.parcela_id = pvd.id
where itemdam.parcela_id = param_id_parcela;
end if;
        if (valor is null) then
            return 0;
end if;
return round(valor, 2);
end getvalorparcelapago;
    function getdatapagamentoparcela(
        param_id_parcela in number,
        param_situacao_parcela in varchar2)
        return date
    as
        datapag date;
begin
        if param_situacao_parcela != 'PAGO' and param_situacao_parcela != 'BAIXADO' and
           param_situacao_parcela != 'PAGO_REFIS' and param_situacao_parcela != 'PAGO_PARCELAMENTO' and
           param_situacao_parcela != 'PAGO_SUBVENCAO' then
            return null;
end if;
        if param_situacao_parcela = 'PAGO_SUBVENCAO' then

select max(sit.datalancamento)
into datapag
from situacaoparcelavalordivida sit
where sit.parcela_id = param_id_parcela
  and sit.situacaoparcela = 'PAGO_SUBVENCAO';
return datapag;
end if;

        if param_situacao_parcela = 'PAGO_PARCELAMENTO' then
select max(coalesce(operacao.data, pgto.dia))
into datapag
from movimentovalorparcelamento pgto
         inner join processoparcelamento parcelamento
                    on parcelamento.id = pgto.parcelamento_id
         left join cancelamentoparcelamento cancelamento
                   on cancelamento.processoparcelamento_id = parcelamento.id
         left join operacaoparcelavalordivida operacao
                   on operacao.id = cancelamento.id
where pgto.parcela_id = param_id_parcela
  and pgto.tipo = 'PAGAMENTO';
if datapag is null then
select max(cancelamento.datacancelamento)
into datapag
from cancelamentoparcelamento cancelamento
         inner join itemprocessoparcelamento ipp
                    on ipp.processoparcelamento_id = cancelamento.processoparcelamento_id
where ipp.parcelavalordivida_id = param_id_parcela;
end if;
return datapag;
end if;
select max(lotebaixa.datapagamento)
into datapag
from itemlotebaixaparcela it
         inner join itemlotebaixa
                    on itemlotebaixa.id = it.itemlotebaixa_id
         inner join lotebaixa
                    on lotebaixa.id = itemlotebaixa.lotebaixa_id
         inner join itemdam
                    on itemdam.id = it.itemdam_id
         inner join parcelavalordivida pvd
                    on pvd.id = itemdam.parcela_id
where (lotebaixa.situacaolotebaixa = 'BAIXADO'
    or lotebaixa.situacaolotebaixa = 'BAIXADO_INCONSITENTE')
  and pvd.id = param_id_parcela;
if datapag is null then
select max(pro.datapagamento)
into datapag
from processodebito pro
         inner join itemprocessodebito item
                    on item.processodebito_id = pro.id
         inner join parcelavalordivida pvd
                    on pvd.id = item.parcela_id
where pvd.id = param_id_parcela;
end if;
        if datapag is null then
select max(atr.datapagamento)
into datapag
from pagamentoavulso atr
where atr.parcelavalordivida_id = param_id_parcela;
end if;
        if datapag is null then
select max(pix.datapagamento)
into datapag
from pix
         inner join dam on pix.id = dam.pix_id
         inner join itemdam on dam.id = itemdam.dam_id
         inner join parcelavalordivida pvd on itemdam.parcela_id = pvd.id
where pvd.id = param_id_parcela;
end if;
return datapag;
end getdatapagamentoparcela;
    function contemincidencia(
        tipo_incidencia in varchar2,
        tipo_acrescimo in varchar2,
        configuracao_acrescimo_id in number)
        return number
    as
        retorno number;
begin
select case
           when max(ia.id) is null
               then 0
           else 1
           end
into retorno
from incidenciaacrescimo ia
where ia.configuracaoacrescimos_id = configuracao_acrescimo_id
  and ia.incidencia = tipo_incidencia
  and ia.tipoacrescimo = tipo_acrescimo
  and rownum = 1;
return retorno;
end contemincidencia;
    function quantidadeimpressoesdam(
        param_id_parcela in number)
        return number
    as
        retorno number;
begin
select count(hist.id)
into retorno
from itemdam it
         inner join dam
                    on dam.id = it.dam_id
         inner join historicoimpressaodam hist
                    on hist.dam_id = dam.id
where it.parcela_id = param_id_parcela;
return retorno;
exception
        when no_data_found then
            return 0;
end quantidadeimpressoesdam;
    function pgtoparccancparcelamento(
        param_id_parcela in number)
        return number
    as
        retorno number;
begin
select sum(valor)
into retorno
from movimentovalorparcelamento pgto
where pgto.parcela_id = param_id_parcela
  and pgto.tipo = 'PAGAMENTO';
--RETURN RETORNO;
-----------------------------------------------------------
if retorno is null then
select sum(valorpago)
into retorno
from pagamentoavulso pgto
where pgto.parcelavalordivida_id = param_id_parcela
  and coalesce(pgto.ativo, 0) = 1;
end if;
return retorno;
-----------------------------------------------------------
exception
        when no_data_found then
            return 0;
end pgtoparccancparcelamento;
    function getvalorimposto(
        param_id_parcela in number,
        calcula_desconto in number,
        param_data_operacao in date)
        return number
    as
        retorno            number;
        juros              number;
        multa              number;
        correcao           number;
        pagto_parcelamento number;
        vencimento         date;
        situacao_parcela   varchar2(75);
        valor_pago         number;
begin
select spvd.situacaoparcela
into situacao_parcela
from situacaoparcelavalordivida spvd
where spvd.id = getultimasituacao(param_id_parcela);
if situacao_parcela = 'PAGO_PARCELAMENTO' then
select coalesce(sum(pagamento.imposto), -1)
into retorno
from pagamentopormovtributaria pagamento
where parcela_id = param_id_parcela;
if retorno = -1 then
select ipp.imposto
into retorno
from itemprocessoparcelamento ipp
where ipp.id =
      (select max(id)
       from itemprocessoparcelamento
       where parcelavalordivida_id = param_id_parcela);
end if;
return round(retorno, 2);
end if;
        if situacao_parcela = 'PARCELADO' then
select sum(ipp.imposto)
into retorno
from itemprocessoparcelamento ipp
         inner join processoparcelamento pp
                    on pp.id = ipp.processoparcelamento_id
where ipp.parcelavalordivida_id = param_id_parcela
  and ipp.id =
      (select max(id)
       from itemprocessoparcelamento
       where parcelavalordivida_id = param_id_parcela);
return round(retorno, 2);
end if;
        if situacao_parcela = 'AGUARDANDO_PAGAMENTO_BLOQUEIO_JUDICIAL' then
select sum(pbj.impostooriginada)
into retorno
from parcelabloqueiojudicial pbj
where pbj.idparcela = param_id_parcela;
return round(retorno, 2);
end if;
        retorno := getvalortipotributo(param_id_parcela, 'IMPOSTO');
        if situacao_parcela = 'BAIXADO' then
select sum(idm.valororiginaldevido)
into valor_pago
from dam dm
         inner join itemdam idm
                    on idm.dam_id = dm.id
where idm.id =
      (select max(s_idm.id)
       from itemdam s_idm
                inner join dam s_dam
                           on s_dam.id = s_idm.dam_id
       where s_dam.situacao = 'PAGO'
         and s_idm.parcela_id = param_id_parcela);
return round(valor_pago * ((retorno * 100) / (retorno + getvalortipotributo(param_id_parcela, 'TAXA'))) /
             100, 2);
end if;
        if situacao_parcela <> 'EM_ABERTO' then
            return retorno;
end if;
        if calcula_desconto = 1 then
            pagto_parcelamento := pgtoparccancparcelamento(param_id_parcela);
            juros := getvalorjuros(param_data_operacao, param_id_parcela, 0);
            multa := getvalormulta(param_data_operacao, param_id_parcela, 0);
select vencimento
into vencimento
from parcelavalordivida
where id = param_id_parcela;
correcao := getvalorcorrecao(param_id_parcela, extract(year from vencimento), 0, param_data_operacao);
            if pagto_parcelamento >= (juros + multa + correcao) then
                pagto_parcelamento := pagto_parcelamento - (juros + multa + correcao);
                if pagto_parcelamento >= retorno then
                    retorno := 0;
else
                    retorno := retorno - pagto_parcelamento;
end if;
end if;
end if;
return retorno;
exception
        when no_data_found then
            return 0;
end getvalorimposto;
    function getvalortaxa(
        param_id_parcela in number,
        calcula_desconto in number,
        param_data_operacao in date)
        return number
    as
        retorno            number;
        juros              number;
        multa              number;
        correcao           number;
        imposto            number;
        pagto_parcelamento number;
        vencimento         date;
        situacao_parcela   varchar2(75);
        valor_pago         number;
begin
select spvd.situacaoparcela
into situacao_parcela
from situacaoparcelavalordivida spvd
where spvd.id = getultimasituacao(param_id_parcela);
if situacao_parcela = 'PAGO_PARCELAMENTO' then
select coalesce(sum(pagamento.taxa), -1)
into retorno
from pagamentopormovtributaria pagamento
where parcela_id = param_id_parcela;
if retorno = -1 then
select ipp.taxa
into retorno
from itemprocessoparcelamento ipp
where ipp.id =
      (select max(id)
       from itemprocessoparcelamento
       where parcelavalordivida_id = param_id_parcela);
end if;
return round(retorno, 2);
end if;
        if situacao_parcela = 'PARCELADO' then
select sum(ipp.taxa)
into retorno
from itemprocessoparcelamento ipp
         inner join processoparcelamento pp
                    on pp.id = ipp.processoparcelamento_id
where ipp.parcelavalordivida_id = param_id_parcela
  and ipp.id =
      (select max(id)
       from itemprocessoparcelamento
       where parcelavalordivida_id = param_id_parcela);
return round(retorno, 2);
end if;
        if situacao_parcela = 'AGUARDANDO_PAGAMENTO_BLOQUEIO_JUDICIAL' then
select sum(pbj.taxaoriginada)
into retorno
from parcelabloqueiojudicial pbj
where pbj.idparcela = param_id_parcela;
return round(retorno, 2);
end if;
        retorno := getvalortipotributo(param_id_parcela, 'TAXA');
        if situacao_parcela = 'BAIXADO' then
select sum(idm.valororiginaldevido)
into valor_pago
from dam dm
         inner join itemdam idm
                    on idm.dam_id = dm.id
where idm.id =
      (select max(s_idm.id)
       from itemdam s_idm
                inner join dam s_dam
                           on s_dam.id = s_idm.dam_id
       where s_dam.situacao = 'PAGO'
         and s_idm.parcela_id = param_id_parcela);
return round(valor_pago * ((retorno * 100) / (retorno + getvalortipotributo(param_id_parcela, 'IMPOSTO'))) /
             100, 2);
end if;
        if situacao_parcela <> 'EM_ABERTO' then
            return retorno;
end if;
        if calcula_desconto = 1 then
            pagto_parcelamento := pgtoparccancparcelamento(param_id_parcela);
            juros := getvalorjuros(param_data_operacao, param_id_parcela, 0);
            multa := getvalormulta(param_data_operacao, param_id_parcela, 0);
select vencimento
into vencimento
from parcelavalordivida
where id = param_id_parcela;
correcao := getvalorcorrecao(param_id_parcela, extract(year from vencimento), 0, param_data_operacao);
            imposto := getvalorimposto(param_id_parcela, 0, param_data_operacao);
            if pagto_parcelamento >= (juros + multa + correcao + imposto) then
                pagto_parcelamento := pagto_parcelamento - (juros + multa + correcao + imposto);
                if pagto_parcelamento >= retorno then
                    retorno := 0;
else
                    retorno := retorno - pagto_parcelamento;
end if;
end if;
end if;
return retorno;
exception
        when no_data_found then
            return 0;
end getvalortaxa;
end pacote_consulta_de_debitos;
