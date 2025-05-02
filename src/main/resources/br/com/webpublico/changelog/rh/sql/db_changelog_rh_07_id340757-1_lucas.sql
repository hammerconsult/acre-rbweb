update pessoafisica pfUpdate
set pfUpdate.datanascimento = (select distinct (select pessoaFisica.DATANASCIMENTO
                                                from PESSOAFISICA_AUD pessoaFisica
                                                where pessoaFisica.ID = pf_Dependente.id
                                                  and pfUpdate.id = pf_Dependente.id
                                                  and pessoaFisica.REV = (select max(revisao.id)
                                                                          from PESSOAFISICA_AUD pf
                                                                                   inner join REVISAOAUDITORIA revisao on pf.rev = revisao.ID
                                                                          where pf.id = pf_Dependente.id
                                                                            and pfUpdate.id = pf.id
                                                                            and pf.DATANASCIMENTO is not null)
                                                  and ROWNUM <= 1)
                               from pessoaFisica pf_Dependente
                                        inner join DEPENDENTE dep on pf_Dependente.ID = dep.DEPENDENTE_ID
                               where pf_Dependente.DATANASCIMENTO is null
                                 and pfUpdate.id = pf_Dependente.id
                                 and exists
                                   (select 1
                                    from PESSOAFISICAPORTAL atualizacao
                                             inner join DependentePortal depPortal
                                                        on atualizacao.ID = depPortal.PESSOAFISICAPORTAL_ID
                                    where depPortal.DEPENDENTE_ID = dep.id
                                      and TO_CHAR(atualizacao.LIBERADOEM, 'DD/MM/YYYY HH24:MI:SS') =
                                          (select TO_CHAR(min(revisao.DATAHORA), 'DD/MM/YYYY HH24:MI:SS')
                                           from PESSOAFISICA_AUD pessoa
                                                    inner join REVISAOAUDITORIA revisao on pessoa.rev = revisao.ID
                                           where pessoa.ID = pf_Dependente.id
                                             and pfUpdate.id = pf_Dependente.id
                                             and revisao.id > (select max(revisao.id)
                                                               from PESSOAFISICA_AUD pf
                                                                        inner join REVISAOAUDITORIA revisao on pf.rev = revisao.ID
                                                               where pf.id = pf_Dependente.id
                                                                 and pfUpdate.id = pf_Dependente.id
                                                                 and pf.DATANASCIMENTO is not null)
                                             and pessoa.DATANASCIMENTO is null)))
where pfUpdate.id in (select distinct pf_Dependente.id
                      from pessoaFisica pf_Dependente
                               inner join DEPENDENTE dep on pf_Dependente.ID = dep.DEPENDENTE_ID
                      where pf_Dependente.DATANASCIMENTO is null
                        and exists
                          (select 1
                           from PESSOAFISICAPORTAL atualizacao
                                    inner join DependentePortal depPortal
                                               on atualizacao.ID = depPortal.PESSOAFISICAPORTAL_ID
                           where depPortal.DEPENDENTE_ID = dep.id
                             and TO_CHAR(atualizacao.LIBERADOEM, 'DD/MM/YYYY HH24:MI:SS') =
                                 (select TO_CHAR(min(revisao.DATAHORA), 'DD/MM/YYYY HH24:MI:SS')
                                  from PESSOAFISICA_AUD pessoa
                                           inner join REVISAOAUDITORIA revisao on pessoa.rev = revisao.ID
                                  where pessoa.ID = pf_Dependente.id
                                    and revisao.id > (select max(revisao.id)
                                                      from PESSOAFISICA_AUD pf
                                                               inner join REVISAOAUDITORIA revisao on pf.rev = revisao.ID
                                                      where pf.id = pf_Dependente.id
                                                        and pf.DATANASCIMENTO is not null)
                                    and pessoa.DATANASCIMENTO is null)));
