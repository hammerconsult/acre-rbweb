INSERT INTO HTMLESTATICOPORTALCONTRIBUINTE (ID, CONFIGURACAO_ID, LOCALHTMLESTATICOPORTALCONTRIBUINTE, DESCRICAOPAGINA,
                                            CORPOPAGINA)
VALUES (HIBERNATE_SEQUENCE.nextval, (SELECT ID FROM CONFIGPORTALCONTRIBUINTE ORDER BY ID DESC FETCH FIRST 1 ROW ONLY),
        'CENTRO_ATENDIMENTO', 'Centros de Atendimento ao Cidadão – CAC’s',
        '<div class="row">
            <div class="col-md-12">
                <h2><span class="text-navy font-bold">CAC - ESTAÇÃO</span></h2>
                <hr/>
                <p>
                    Mercado Francisco Marinheiro, Box 03 e 04<br/>
                    Endereço: Rua Sorocaba, Esquina com Av. Ceará, Estação Experimental<br/>
                    Dias e horário de atendimento: de segunda a sexta-feira das 7h às 18h
                </p>
                <p>Gerente: Elenir do Nascimento Oliveira</p>
                <hr/>

                <h2><span class="text-navy font-bold">CAC - OCA</span></h2>
                <hr/>
                <p>
                    Praça Vermelha - PMRB<br/>
                    Endereço: Rua Quintino Bocaiuva, 299 - Centro<br/>
                    Dias e horário de atendimento: de segunda a sexta-feira das 7h30min às 13h30min<br/>
                    Telefone de atendimento: (68) 3215-2438
                </p>
                <p>Gerente: Eriane da Silva Nobre</p>
                <hr/>

                <h2><span class="text-navy font-bold">CAC - XV</span></h2>
                <hr/>
                <p>
                    Endereço: Mercado Municipal José Júlio Saldanha Braga (Mercado do XV), Rodovia Rua Boulevard Augusto Monteiro, Nº 395, Box: 08, CEP: 69901-230, Bairro Quinze.<br/>
                    Dias e horário de atendimento: de segunda a sexta-feira das 7h às 14h
                </p>
                <p>Gerente: Sérgio Roberto da Costa Matos</p>
                <hr/>

                <h2><span class="text-navy font-bold">CAC - SOBRAL</span></h2>
                <hr/>
                <p>
                    Mercado Luiz Galvez, Box 80<br/>
                    Endereço: Estrada do Sobral, s/n - Sobral<br/>
                    Dias e horário de atendimento: de segunda a sexta-feira das 7h às 14h<br/>
                    Telefone de atendimento: (68) 3242-2132
                </p>
                <p>Gerente: Luciano Mota Brandão</p>
                <hr/>
            </div>
        </div>')
