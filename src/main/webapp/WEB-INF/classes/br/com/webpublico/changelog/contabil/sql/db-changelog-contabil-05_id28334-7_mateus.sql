insert into viagemdiaria(id, propostaConcessaoDiaria_id, tipoviagem, itinerario, meiodetransporte, horasaida, minutosaida, horachegada, minutochegada, transladocompernoite, inicio, fim)
(select hibernate_sequence.nextval, id, tipoviagem, etinerario, meiodetransporte, horasaida, minutosaida, horachegada, minutochegada, transladocompernoite, trunc(inicio), trunc(fim) from propostaconcessaodiaria)
