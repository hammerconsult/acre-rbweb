update CertidaoDividaAtiva set SITUACAOCERTIDAODA = 'QUITADA'
            where id in (select CDA.id from CertidaoDividaAtiva cda
            inner join ItemCertidaoDividaAtiva icda on icda.CERTIDAO_ID = cda.id
            inner join ITEMINSCRICAODIVIDAATIVA iida on iida.id = icda.ITEMINSCRICAODIVIDAATIVA_ID
            inner join Exercicio exCda on exCda.id = cda.exercicio_id
            inner join ValorDivida vd on vd.calculo_id = iida.id
            inner join Exercicio ex on ex.id = vd.exercicio_id
            inner join ParcelaValorDivida pvd on pvd.valorDivida_id = vd.id
            inner join SituacaoparcelaValorDivida spvd on spvd.id = pvd.situacaoatual_id
            where spvd.SITUACAOPARCELA = 'BAIXADO'
            and cda.SITUACAOCERTIDAODA = 'ABERTA'
            and (select count(ic.id) from ItemCertidaoDividaAtiva ic where ic.CERTIDAO_ID = cda.id) = 1)
