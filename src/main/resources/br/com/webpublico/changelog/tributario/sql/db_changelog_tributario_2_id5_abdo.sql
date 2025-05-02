 update laudoavaliacaoitbi laudo
 set laudo.datavencimento = (select itbi.vencimento
                                from calculoitbi itbi
                             where itbi.id = laudo.calculoitbi_id)