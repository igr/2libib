# 2libib

Import knjiga u **libib.com** iz sledećih izvora, uključujući i sliku naslovne strane:

+ **delfi.rs** - potreban `ID` knjige iz URL ili iz devtools.
+ **goodreads.com** - potreban samo URL knjige.
+ _ručno definisane_ - brže je kada postoji dobar URL omota.

Preporuka: napravite kategoriju "Inbox" u **libib.com** i importujte knjige u njega, kako biste mogli da ih pregledate pre nego što ih premestite u odgovarajuću kategoriju.

Obavezno proverite da li je importovana knjiga ispravna, jer ne postoji nikakva posebna detekcija grešaka.

## Konfiguracija

Libib ID ciljne kategorije je hardkodovan u aplikaciji 🤷‍♂️.

`.env` sadrži sledeće promenljive:

```
USERNAME=libib_username
PASSWORD=libib_password
```
