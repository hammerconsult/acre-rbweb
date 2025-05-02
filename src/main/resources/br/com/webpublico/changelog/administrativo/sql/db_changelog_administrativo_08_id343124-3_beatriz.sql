create or replace procedure proc_popular_documento_anexos_pt_2 is
    detentor_id number(19);
    ultimo_id   number(19);
begin
    --corrige recursolicitacao
    ultimo_id := null;
for registro in (select dacl.RECURSOLICITACAO_ID,
                            dacl.exibirportal,
                            dacl.tipodocumentoanexo_id,
                            ac.dataupload,
                            a.id as arquivo_id
                     from RECURSOLICITACAO rl
                              inner join detentorarqcomplicitacao dacl on dacl.RECURSOLICITACAO_ID = rl.id
                              inner join detentorarquivocomposicao dac on dac.id = dacl.detentorarquivocomposicao_id
                              inner join arquivocomposicao ac on ac.detentorarquivocomposicao_id = dac.id
                              inner join arquivo a on a.id = ac.arquivo_id
                     where rl.detentordocumentolicitacao_id is null
                     order by rl.id)
        loop
            if (ultimo_id != registro.RECURSOLICITACAO_ID) then
select hibernate_sequence.nextval
into detentor_id
from dual;
insert into detentordocumentolicitacao (id)
values (detentor_id);
update RECURSOLICITACAO
set detentordocumentolicitacao_id = detentor_id
where id = registro.RECURSOLICITACAO_ID;
end if;

            ultimo_id := registro.RECURSOLICITACAO_ID;

insert into documentolicitacao (id, detentordocumentolicitacao_id, tipodocumentoanexo_id,
                                tiporegistro, arquivo_id, link, exibirportaltransparencia,
                                enviarpncc, observacao, dataregistro)
values (hibernate_sequence.nextval, detentor_id, registro.tipodocumentoanexo_id, 'ARQUIVO',
        registro.arquivo_id, null, registro.exibirportal, 0, null, registro.dataupload);
end loop;

    --corrige registrosolmatext (registro de preço externo)
    ultimo_id := null;
for registro in (select dacl.REGISTROSOLMATEXTERNO_ID,
                            dacl.exibirportal,
                            dacl.tipodocumentoanexo_id,
                            ac.dataupload,
                            a.id as arquivo_id
                     from REGISTROSOLMATEXT reg
                              inner join detentorarqcomplicitacao dacl on dacl.REGISTROSOLMATEXTERNO_ID = reg.id
                              inner join detentorarquivocomposicao dac on dac.id = dacl.detentorarquivocomposicao_id
                              inner join arquivocomposicao ac on ac.detentorarquivocomposicao_id = dac.id
                              inner join arquivo a on a.id = ac.arquivo_id
                     where reg.detentordocumentolicitacao_id is null
                     order by reg.id)
        loop
            if (ultimo_id != registro.REGISTROSOLMATEXTERNO_ID) then
select hibernate_sequence.nextval
into detentor_id
from dual;
insert into detentordocumentolicitacao (id)
values (detentor_id);
update REGISTROSOLMATEXT
set detentordocumentolicitacao_id = detentor_id
where id = registro.REGISTROSOLMATEXTERNO_ID;
end if;

            ultimo_id := registro.REGISTROSOLMATEXTERNO_ID;

insert into documentolicitacao (id, detentordocumentolicitacao_id, tipodocumentoanexo_id,
                                tiporegistro, arquivo_id, link, exibirportaltransparencia,
                                enviarpncc, observacao, dataregistro)
values (hibernate_sequence.nextval, detentor_id, registro.tipodocumentoanexo_id, 'ARQUIVO',
        registro.arquivo_id, null, registro.exibirportal, 0, null, registro.dataupload);
end loop;

    --corrige repactuacaopreco
    ultimo_id := null;
for registro in (select dacl.REPACTUACAOPRECO_ID,
                            dacl.exibirportal,
                            dacl.tipodocumentoanexo_id,
                            ac.dataupload,
                            a.id as arquivo_id
                     from REPACTUACAOPRECO rp
                              inner join detentorarqcomplicitacao dacl on dacl.REPACTUACAOPRECO_ID = rp.id
                              inner join detentorarquivocomposicao dac on dac.id = dacl.detentorarquivocomposicao_id
                              inner join arquivocomposicao ac on ac.detentorarquivocomposicao_id = dac.id
                              inner join arquivo a on a.id = ac.arquivo_id
                     where rp.detentordocumentolicitacao_id is null
                     order by rp.id)
        loop
            if (ultimo_id != registro.REPACTUACAOPRECO_ID) then
select hibernate_sequence.nextval
into detentor_id
from dual;
insert into detentordocumentolicitacao (id)
values (detentor_id);
update REPACTUACAOPRECO
set detentordocumentolicitacao_id = detentor_id
where id = registro.REPACTUACAOPRECO_ID;
end if;

            ultimo_id := registro.REPACTUACAOPRECO_ID;

insert into documentolicitacao (id, detentordocumentolicitacao_id, tipodocumentoanexo_id,
                                tiporegistro, arquivo_id, link, exibirportaltransparencia,
                                enviarpncc, observacao, dataregistro)
values (hibernate_sequence.nextval, detentor_id, registro.tipodocumentoanexo_id, 'ARQUIVO',
        registro.arquivo_id, null, registro.exibirportal, 0, null, registro.dataupload);
end loop;

    --corrige responsaveltecnicofiscal
    ultimo_id := null;
for registro in (select dacl.RESPONSAVELTECNICOFISCAL_ID,
                            dacl.exibirportal,
                            dacl.tipodocumentoanexo_id,
                            ac.dataupload,
                            a.id as arquivo_id
                     from RESPONSAVELTECNICOFISCAL rtf
                              inner join detentorarqcomplicitacao dacl on dacl.RESPONSAVELTECNICOFISCAL_ID = rtf.id
                              inner join detentorarquivocomposicao dac on dac.id = dacl.detentorarquivocomposicao_id
                              inner join arquivocomposicao ac on ac.detentorarquivocomposicao_id = dac.id
                              inner join arquivo a on a.id = ac.arquivo_id
                     where rtf.detentordocumentolicitacao_id is null
                     order by rtf.id)
        loop
            if (ultimo_id != registro.RESPONSAVELTECNICOFISCAL_ID) then
select hibernate_sequence.nextval
into detentor_id
from dual;
insert into detentordocumentolicitacao (id)
values (detentor_id);
update RESPONSAVELTECNICOFISCAL
set detentordocumentolicitacao_id = detentor_id
where id = registro.RESPONSAVELTECNICOFISCAL_ID;
end if;

            ultimo_id := registro.RESPONSAVELTECNICOFISCAL_ID;

insert into documentolicitacao (id, detentordocumentolicitacao_id, tipodocumentoanexo_id,
                                tiporegistro, arquivo_id, link, exibirportaltransparencia,
                                enviarpncc, observacao, dataregistro)
values (hibernate_sequence.nextval, detentor_id, registro.tipodocumentoanexo_id, 'ARQUIVO',
        registro.arquivo_id, null, registro.exibirportal, 0, null, registro.dataupload);
end loop;
end ;
