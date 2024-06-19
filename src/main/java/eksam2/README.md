# Korraldav koreograafiakeel Koit

Koit on [koreograafiline programmeerimiskeel](https://en.wikipedia.org/wiki/Choreographic_programming), 
mis võimaldab korraldada andmevahetust kahe näitleja (_actor_) Alice'i ja Bob-i vahel.
Mõlemal näitlejal on oma väärtuskeskkond; nende vahel saab väärtusi saata ja näitlejad võivad ka lokaalseid arvutusi teha.
Näiteks võib kirjutada selles keeles järgmise programmi:
```
Alice.x -> Bob.x
Alice.(x + 1) -> Bob.y
if Bob?(x + y) then
    Bob.(tmp = 10 * x)
    Alice.tmp -> Bob.kala
    Bob.(kala + 1) -> Alice.result
else
    Bob.(10 * y) -> Alice.result
endif
return result
```
Selles programmis saadab Alice kõigepealt oma muutuja `x` väärtuse Bob-ile muutjasse `x`.
Lisaks saadab Alice avaldise `x + 1` (kus `x` on Alice'i väärtuskeskkonnast) väärtuse Bob-ile muutujasse `y`.
Seejärel on tingimuslause, mille tingimuse väärtustab Bob oma keskkonnas.
Tõeses harus teeb Bob kõigepealt ühe lokaalse omistamise oma muutujasse `tmp`.
Samas Alice saadab oma `tmp` muutuja Bob-i muutujasse `kala`.
Viimaks saadab Bok `kala + 1` väärtuse Alice'i muutujasse `result`.
Vääras harus saadetakse samuti sinna mingi väärtus.
Kõige lõpuks tagastab programm Alice'i `result` muutuja väärtuse.


## AST

Keele AST klassid paiknevad _eksam2.ast_ paketis ja nende ülemklassiks on _KoitNode_:

* _KoitExpr_ — avaldised, alamklassidega: 
    * _KoitNum_ — arvliteraal;
    * _KoitVar_ — muutuja;
    * _KoitAdd_ — liitmine;
    * _KoitMul_ — korrutamine;
* _KoitStmt_ — laused, alamklassidega:
    * _KoitRet_ — tagastuslause;
    * _KoitSend_ — saatmine (ehk üldistatud omistamine);
    * _KoitBlock_ — lausete plokk;
    * _KoitCheck_ — tingimuslause.

Klassis _KoitNode_ on staatilised abimeetodid, millega saab mugavamalt abstraktseid süntaksipuid luua.
Ülalolev avaldis moodustatakse järgmiselt:
```
block(
        send(Alice, Bob, "x", var("x")),
        send(Alice, Bob, "y", add(var("x"), num(1))),
        check(Bob, add(var("x"), var("y")),
                block(
                        send(Bob, Bob, "tmp", mul(num(10), var("x"))),
                        send(Alice, Bob, "kala", var("tmp")),
                        send(Bob, Alice, "result", add(var("kala"), num(1)))),
                block(
                        send(Bob, Alice, "result", mul(num(10), var("y"))))),
        ret(var("result")))
```

## Alusosa: KoitEvaluator

Klassis _KoitEvaluator_ tuleb implementeerida meetod _eval_, mis väärtustab Koit keele lause etteantud väärtuskeskkonnas.
Väärtustamisele kehtivad järgmised nõuded:

1. Arvliteraalid, muutujad ja aritmeetilised operaatorid käituvad standardselt.
2. Mõlemal näitlejal on omaette väärtuskeskkond. Muutuja väärtus leitakse hetkel aktiivse näitleja väärtuskkeskkonnast.
3. Plokid ja tingimuslaused käituvad standardselt. Tingimuslause tingimus väärtustatakse vastava aktiivse näitlejaga. Tingimuslause tingimus on väär, kui avaldise väärtus on 0, ja tõene, kui avaldise väärtus erineb 0-st.
4. Etteantud lause väärtustamise tulemuseks on esimesena väärtustatud tagastuslause avaldise väärtus (Alice on alati aktiivne näitleja). See tähendab, et tagastuslause lõpetab programmi väärtustamise koheselt — järgnevaid ploki lauseid ei väärtustata.
5. Saatmine väärtustab avaldise saatja näitlejaga ja omistab selle väärtuse vastuvõtja väärtuskkeskkonda. Saatja ja vastuvõtja võivad olla samad — siis on tegu tavalise omistamisega.
6. Defineerimata muutuja väärtustamisel või defineerimata muutujasse vastu võtmisel visatakse _KoitException_.


## Põhiosa: KoitAst

Failis _Koit.g4_ tuleb implementeerida grammatika ja klassis _KoitAst_ tuleb implementeerida meetod _parseTreeToAst_, mis teisendab parsepuu AST-iks.
Süntaksile kehtivad järgmised nõuded:

1. Arvliteraalid koosnevad numbritest, sealhulgas null. 
2. Muutujad koosnevad suurtest või väikestest ladina tähtedest.
3. Term võib olla arvliteraal, muutuja või sulgudes avaldis (vt allpool).
4. Avaldis võib olla term, binaarne avaldiste liitmine (`+`) või binaarne avaldiste korrutamine (`*`). Binaarsed operaatorid on vasakassotsiatiivsed. Nende puhul kehtuvad tehete standardsed prioriteedid, mis kahanevas järjekorras on: korrutamine, liitmine.
5. Tagastuslause koosneb võtmesõnast `return`, millele järgneb avaldis.
6. Saatmislause koosneb saatja nimest, punktist (`.`), termist, noolest (`->`), vastuvõtja nimest, punktist (`.`) ja muutuja nimest.
7. Näitleja nimi võib olla `Alice` või `Bob`.
8. Lisaks on lubatud süntaktiline suhkur näitleja lokaalseks omistamiseks, näiteks `Alice.(x = e)` tuleb AST-is esitada kui `Alice.(e) -> Alice.x`.
9. Tingimuslause koosneb võtmesõnast `if`, näitleja nimest, küsimärgist (`?`), võtmesõnast `then`, plokist, võtmesõnast `else`, plokist ja võtmesõnast `endif`.
10. Plokk on ühe või rohkema lause jada, mille vahel eraldajaid pole.
11. Programm on samuti plokk.
12. Tühisümboleid (tühikud, tabulaatorid, reavahetused) tuleb ignoreerida.


## Lõviosa: KoitCompiler

Klassis _KoitCompiler_ tuleb implementeerida meetod _compile_, mis kompileerib Koit keele lause CMa programmiks. 
Kompileerimisele kehtivad järgmised nõuded:

1. Muutujate väärtused antakse stack'il etteantud järjekorras. Kõigepealt kõik Alice'i muutujad ja seejärel kõik Bob-i muutujad.
2. Programmi väärtustamise lõpuks peab stack'ile lisanduma täpselt üks element: programmi väärtustamise tulemus, mis on sama nagu _KoitEvaluator_-iga väärtustades.
3. Defineerimata muutuja väärtustamisel või defineerimata muutujasse vastu võtmisel visatakse _KoitException_ **kompileerimise ajal**.
