update parcelamentojudicial set numeroprocessoforum =
    (select pj.numeroprocessoforum from processojudicial pj where pj.id = parcelamentojudicial.processojudicial_id)
where numeroprocessoforum is null
