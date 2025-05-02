update borderopagamentoextra bp set bp.contacorrentefavorecido_id = (
  select p.contacorrentebancaria_id from pagamentoextra p
  where p.id = bp.pagamentoextra_id
)

