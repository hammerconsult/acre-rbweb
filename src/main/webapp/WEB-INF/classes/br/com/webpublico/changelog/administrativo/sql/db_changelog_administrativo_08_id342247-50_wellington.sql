create or replace procedure proc_popular_documento_licitacao is
    detentor_id number(19);
    ultimo_id   number(19);
begin
    --corrige solicitacaomaterial
    for registro in (select dacl.solicitacaomaterial_id,
                            dacl.exibirportal,
                            dacl.tipodocumentoanexo_id,
                            ac.dataupload,
                            a.id as arquivo_id
                     from detentorarqcomplicitacao dacl
                              inner join solicitacaomaterial sm on sm.id = dacl.solicitacaomaterial_id
                              inner join detentorarquivocomposicao dac on dac.id = dacl.detentorarquivocomposicao_id
                              inner join arquivocomposicao ac on ac.detentorarquivocomposicao_id = dac.id
                              inner join arquivo a on a.id = ac.arquivo_id
                     where sm.detentordocumentolicitacao_id is null
                     order by sm.id)
        loop
            if (ultimo_id != registro.solicitacaomaterial_id) then
                select hibernate_sequence.nextval
                into detentor_id
                from dual;
                insert into detentordocumentolicitacao (id)
                values (detentor_id);
                update solicitacaomaterial
                set detentordocumentolicitacao_id = detentor_id
                where id = registro.solicitacaomaterial_id;
            end if;

            ultimo_id
                := registro.solicitacaomaterial_id;

            insert into documentolicitacao (id, detentordocumentolicitacao_id, tipodocumentoanexo_id,
                                            tiporegistro, arquivo_id, link, exibirportaltransparencia,
                                            enviarpncc, observacao, dataregistro)
            values (hibernate_sequence.nextval, detentor_id, registro.tipodocumentoanexo_id, 'ARQUIVO',
                    registro.arquivo_id, null, registro.exibirportal, 0, null, registro.dataupload);
        end loop;
    --corrige licitacao
    ultimo_id
        := null;
    for registro in (select dacl.licitacao_id,
                            dacl.exibirportal,
                            dacl.tipodocumentoanexo_id,
                            ac.dataupload,
                            a.id as arquivo_id
                     from licitacao l
                              inner join detentorarqcomplicitacao dacl on dacl.licitacao_id = l.id
                              inner join detentorarquivocomposicao dac on dac.id = dacl.detentorarquivocomposicao_id
                              inner join arquivocomposicao ac on ac.detentorarquivocomposicao_id = dac.id
                              inner join arquivo a on a.id = ac.arquivo_id
                     where l.detentordocumentolicitacao_id is null
                     order by l.id)
        loop
            if (ultimo_id != registro.licitacao_id) then
                select hibernate_sequence.nextval
                into detentor_id
                from dual;
                insert into detentordocumentolicitacao (id)
                values (detentor_id);
                update licitacao
                set detentordocumentolicitacao_id = detentor_id
                where id = registro.licitacao_id;
            end if;

            ultimo_id
                := registro.licitacao_id;

            insert into documentolicitacao (id, detentordocumentolicitacao_id, tipodocumentoanexo_id,
                                            tiporegistro, arquivo_id, link, exibirportaltransparencia,
                                            enviarpncc, observacao, dataregistro)
            values (hibernate_sequence.nextval, detentor_id, registro.tipodocumentoanexo_id, 'ARQUIVO',
                    registro.arquivo_id, null, registro.exibirportal, 0, null, registro.dataupload);
        end loop;
    --corrige dispensadelicitacao
    ultimo_id
        := null;
    for registro in (select dacl.dispensadelicitacao_id,
                            dacl.exibirportal,
                            dacl.tipodocumentoanexo_id,
                            ac.dataupload,
                            a.id as arquivo_id
                     from dispensadelicitacao dl
                              inner join detentorarqcomplicitacao dacl on dacl.dispensadelicitacao_id = dl.id
                              inner join detentorarquivocomposicao dac on dac.id = dacl.detentorarquivocomposicao_id
                              inner join arquivocomposicao ac on ac.detentorarquivocomposicao_id = dac.id
                              inner join arquivo a on a.id = ac.arquivo_id
                     where dl.detentordocumentolicitacao_id is null
                     order by dl.id)
        loop
            if (ultimo_id != registro.dispensadelicitacao_id) then
                select hibernate_sequence.nextval
                into detentor_id
                from dual;
                insert into detentordocumentolicitacao (id)
                values (detentor_id);
                update dispensadelicitacao
                set detentordocumentolicitacao_id = detentor_id
                where id = registro.dispensadelicitacao_id;
            end if;

            ultimo_id
                := registro.dispensadelicitacao_id;

            insert into documentolicitacao (id, detentordocumentolicitacao_id, tipodocumentoanexo_id,
                                            tiporegistro, arquivo_id, link, exibirportaltransparencia,
                                            enviarpncc, observacao, dataregistro)
            values (hibernate_sequence.nextval, detentor_id, registro.tipodocumentoanexo_id, 'ARQUIVO',
                    registro.arquivo_id, null, registro.exibirportal, 0, null, registro.dataupload);
        end loop;
    --corrige ataregistropreco
    ultimo_id
        := null;
    for registro in (select dacl.ataregistropreco_id,
                            dacl.exibirportal,
                            dacl.tipodocumentoanexo_id,
                            ac.dataupload,
                            a.id as arquivo_id
                     from ataregistropreco arp
                              inner join detentorarqcomplicitacao dacl on dacl.ataregistropreco_id = arp.id
                              inner join detentorarquivocomposicao dac on dac.id = dacl.detentorarquivocomposicao_id
                              inner join arquivocomposicao ac on ac.detentorarquivocomposicao_id = dac.id
                              inner join arquivo a on a.id = ac.arquivo_id
                     where arp.detentordocumentolicitacao_id is null
                     order by arp.id)
        loop
            if (ultimo_id != registro.ataregistropreco_id) then
                select hibernate_sequence.nextval
                into detentor_id
                from dual;
                insert into detentordocumentolicitacao (id)
                values (detentor_id);
                update ataregistropreco
                set detentordocumentolicitacao_id = detentor_id
                where id = registro.ataregistropreco_id;
            end if;

            ultimo_id
                := registro.ataregistropreco_id;

            insert into documentolicitacao (id, detentordocumentolicitacao_id, tipodocumentoanexo_id,
                                            tiporegistro, arquivo_id, link, exibirportaltransparencia,
                                            enviarpncc, observacao, dataregistro)
            values (hibernate_sequence.nextval, detentor_id, registro.tipodocumentoanexo_id, 'ARQUIVO',
                    registro.arquivo_id, null, registro.exibirportal, 0, null, registro.dataupload);
        end loop;
    --corrige solicitacaomaterialext
    ultimo_id
        := null;
    for registro in (select dacl.solicitacaomaterialexterno_id,
                            dacl.exibirportal,
                            dacl.tipodocumentoanexo_id,
                            ac.dataupload,
                            a.id as arquivo_id
                     from solicitacaomaterialext sme
                              inner join detentorarqcomplicitacao dacl on dacl.solicitacaomaterialexterno_id = sme.id
                              inner join detentorarquivocomposicao dac on dac.id = dacl.detentorarquivocomposicao_id
                              inner join arquivocomposicao ac on ac.detentorarquivocomposicao_id = dac.id
                              inner join arquivo a on a.id = ac.arquivo_id
                     where sme.detentordocumentolicitacao_id is null
                     order by sme.id)
        loop
            if (ultimo_id != registro.solicitacaomaterialexterno_id) then
                select hibernate_sequence.nextval
                into detentor_id
                from dual;
                insert into detentordocumentolicitacao (id)
                values (detentor_id);
                update solicitacaomaterialext
                set detentordocumentolicitacao_id = detentor_id
                where id = registro.solicitacaomaterialexterno_id;
            end if;

            ultimo_id
                := registro.solicitacaomaterialexterno_id;

            insert into documentolicitacao (id, detentordocumentolicitacao_id, tipodocumentoanexo_id,
                                            tiporegistro, arquivo_id, link, exibirportaltransparencia,
                                            enviarpncc, observacao, dataregistro)
            values (hibernate_sequence.nextval, detentor_id, registro.tipodocumentoanexo_id, 'ARQUIVO',
                    registro.arquivo_id, null, registro.exibirportal, 0, null, registro.dataupload);
        end loop;
    --corrige contrato
    ultimo_id
        := null;
    for registro in (select dacl.contrato_id,
                            dacl.exibirportal,
                            dacl.tipodocumentoanexo_id,
                            ac.dataupload,
                            a.id as arquivo_id
                     from contrato c
                              inner join detentorarqcomplicitacao dacl on dacl.contrato_id = c.id
                              inner join detentorarquivocomposicao dac on dac.id = dacl.detentorarquivocomposicao_id
                              inner join arquivocomposicao ac on ac.detentorarquivocomposicao_id = dac.id
                              inner join arquivo a on a.id = ac.arquivo_id
                     where c.detentordocumentolicitacao_id is null
                     order by c.id)
        loop
            if (ultimo_id != registro.contrato_id) then
                select hibernate_sequence.nextval
                into detentor_id
                from dual;
                insert into detentordocumentolicitacao (id)
                values (detentor_id);
                update contrato
                set detentordocumentolicitacao_id = detentor_id
                where id = registro.contrato_id;
            end if;

            ultimo_id
                := registro.contrato_id;

            insert into documentolicitacao (id, detentordocumentolicitacao_id, tipodocumentoanexo_id,
                                            tiporegistro, arquivo_id, link, exibirportaltransparencia,
                                            enviarpncc, observacao, dataregistro)
            values (hibernate_sequence.nextval, detentor_id, registro.tipodocumentoanexo_id, 'ARQUIVO',
                    registro.arquivo_id, null, registro.exibirportal, 0, null, registro.dataupload);
        end loop;
    --corrige aditivocontrato
    ultimo_id
        := null;
    for registro in (select ac.id   aditivocontrato_id,
                            0    as exibirportal,
                            null as tipodocumentoanexo_id,
                            ac.dataupload,
                            a.id as arquivo_id
                     from aditivocontrato ac
                              inner join detentorarquivocomposicao dac on dac.id = ac.detentorarquivocomposicao_id
                              inner join arquivocomposicao ac on ac.detentorarquivocomposicao_id = dac.id
                              inner join arquivo a on a.id = ac.arquivo_id
                     where ac.detentordocumentolicitacao_id is null
                     order by ac.id)
        loop
            if (ultimo_id != registro.aditivocontrato_id) then
                select hibernate_sequence.nextval
                into detentor_id
                from dual;
                insert into detentordocumentolicitacao (id)
                values (detentor_id);
                update aditivocontrato
                set detentordocumentolicitacao_id = detentor_id
                where id = registro.aditivocontrato_id;
            end if;

            ultimo_id
                := registro.aditivocontrato_id;

            insert into documentolicitacao (id, detentordocumentolicitacao_id, tipodocumentoanexo_id,
                                            tiporegistro, arquivo_id, link, exibirportaltransparencia,
                                            enviarpncc, observacao, dataregistro)
            values (hibernate_sequence.nextval, detentor_id, registro.tipodocumentoanexo_id, 'ARQUIVO',
                    registro.arquivo_id, null, registro.exibirportal, 0, null, registro.dataupload);
        end loop;
    --corrige apostilamentocontrato
    ultimo_id
        := null;
    for registro in (select ac.id   apostilamentocontrato_id,
                            0    as exibirportal,
                            null as tipodocumentoanexo_id,
                            ac.dataupload,
                            a.id as arquivo_id
                     from apostilamentocontrato ac
                              inner join detentorarquivocomposicao dac on dac.id = ac.detentorarquivocomposicao_id
                              inner join arquivocomposicao ac on ac.detentorarquivocomposicao_id = dac.id
                              inner join arquivo a on a.id = ac.arquivo_id
                     where ac.detentordocumentolicitacao_id is null
                     order by ac.id)
        loop
            if (ultimo_id != registro.apostilamentocontrato_id) then
                select hibernate_sequence.nextval
                into detentor_id
                from dual;
                insert into detentordocumentolicitacao (id)
                values (detentor_id);
                update apostilamentocontrato
                set detentordocumentolicitacao_id = detentor_id
                where id = registro.apostilamentocontrato_id;
            end if;

            ultimo_id
                := registro.apostilamentocontrato_id;

            insert into documentolicitacao (id, detentordocumentolicitacao_id, tipodocumentoanexo_id,
                                            tiporegistro, arquivo_id, link, exibirportaltransparencia,
                                            enviarpncc, observacao, dataregistro)
            values (hibernate_sequence.nextval, detentor_id, registro.tipodocumentoanexo_id, 'ARQUIVO',
                    registro.arquivo_id, null, registro.exibirportal, 0, null, registro.dataupload);
        end loop;
end;
