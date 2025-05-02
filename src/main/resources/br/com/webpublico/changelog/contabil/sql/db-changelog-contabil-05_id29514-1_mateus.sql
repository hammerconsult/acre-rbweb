update DiariaContabilizacao dc set dc.tipoProposta =
(select prop.tipoproposta from PropostaConcessaoDiaria prop where prop.id = dc.PROPOSTACONCESSAODIARIA_ID)
where dc.tipoProposta is null
