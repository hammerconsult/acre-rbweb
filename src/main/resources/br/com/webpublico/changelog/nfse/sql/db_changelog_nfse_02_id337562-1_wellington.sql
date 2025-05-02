delete
from OAUTH_ACCESS_TOKEN
where AUTHENTICATION_ID in (
    select AUTHENTICATION_ID
    from (select AUTHENTICATION_ID, count(1)
          from OAUTH_ACCESS_TOKEN
          group by AUTHENTICATION_ID
          having count(1) > 1))
