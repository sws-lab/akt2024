# Otsustav oraaklikeel Ott

Ott on otsusavaldistega programmeerimiskeel, mis kasutab oraaklit, et teha otsuseid valikuoperaatorite argumentide vahel.
Avaldised võivad sisaldada täisarve, muutujaid ja lihtsaid aritmeetilisi operatsioone.
Kogu programm on selles keeles avaldis, s.t. meil on omistamisavaldis ning avaldiste järjest väärtustamise avaldis.

Näiteks võib kirjutada selles keeles järgmise programmi:
```
(x = 10; y = 12; x + 3 | y - 1) + (x | y)
```
See liidab kokku kahte alamavaldist. 
Esimene liidetav omistab kõigepealt muutujatele `x` ja `y` vastavalt arvud 10 ja 12.
Seejärel küsitakse oraakli käest, kas väärtustada tuleks `x + 3` või `y - 1`, mis võib olla 13 või 11. 
Teine liidetav laseb oraaklil valida muutujate `x` ja `y` vahel, mis on nüüdseks 10 või 12. 
Sõltuvalt oraakli valikutest on programmi väärtustamise tulemuseks 21, 23 või 25
(siin on neli erinevat võimalust, aga kaks neist annavad sama summa).


## AST

Keele AST klassid paiknevad _eksam1.ast_ paketis ja nende ülemklassiks on _OttNode_:

* _OttNum_ — arvliteraal;
* _OttAdd_ — liitmine;
* _OttSub_ — lahutamine;
* _OttVar_ — muutuja;
* _OttAssign_ — omistamisavaldis;
* _OttSeq_ — avaldiste järjest väärtustamise avaldis;
* _OttDecision_ — otsusavaldis.

Klassis _OttNode_ on staatilised abimeetodid, millega saab mugavamalt abstraktseid süntaksipuid luua.
Ülalolev avaldis moodustatakse järgmiselt:
```
add(
    seq(
        assign("x", num(10)),
        assign("y", num(12)),
        decision(
            add(var("x"), num(3)),
            sub(var("y"), num(1))
        )
    ),
    decision(var("x"), var("y"))
)
```

## Alusosa: OttEvaluator

Klassis _OttEvaluator_ tuleb implementeerida meetod _eval_, mis väärtustab Ott keele avaldise etteantud väärtuskeskkonnas.
Tal on lisaks ka argumendiks kaasas [BooleanSupplier](https://docs.oracle.com/en%2Fjava%2Fjavase%2F21%2Fdocs%2Fapi%2F%2F/java.base/java/util/function/BooleanSupplier.html) tüüpi oraakel, millega teostatakse valikud otsusavaldise väärtustamisel.

Väärtustamisele kehtivad järgmised nõuded:

1. Arvliteraalid, muutujad ja binaarsed operatsioonid käituvad standardselt.
2. Binaarsete operaatorite vasak argument tuleb väärtustada enne paremat argumenti.
3. Omistamisavaldise (_Assign_) väärtustamisel tuleb väärtustada parempoolne avaldis ja omistada väärtuskeskkonnas muutujale. Omistamisvaldise enda väärtuseks on omistatud väärtus. Seega peaks käituma täpselt nagu Javas: avaldise `(x = 6) + x` väärtustamise tulemuseks on 12.
4. Avaldiste järjest väärtustamisel (_Seq_) tuleb kõik avaldised järjest vasakult paremale väärtustada ja tagastada viimase avaldise väärtus. Iga järgneva avaldise väärtustamisel tuleks kasutada eelmise avaldise järgset väärtuskeskkonda: avaldise `x = 5; y = x + 1; x + y` väärtustamise tulemuseks on 11.
5. Otsusavaldis (_Decision_) kasutab oraaklit otsustamaks, kas valida vasakpoolne või parempoolne argument. Oraakli käest tuleb küsida valik (_getAsBoolean()_ meetodi abil): kui vastus on `true`, siis tuleb väärtustada vasakpoolne avaldis; kui vastus on `false`, siis tuleb väärtustada parempoolne.
6. Otsusavaldises väärtustatakse ainult valitud alamavaldis.
7. Võib eeldada, et defineerimata muutujaid ei kasutata.


## Põhiosa: OttAst

Failis _Ott.g4_ tuleb implementeerida grammatika ja klassis _OttAst_ tuleb implementeerida meetod _parseTreeToAst_, mis teisendab parsepuu AST-iks.
Süntaksile kehtivad järgmised nõuded:

1. Arvliteraalid koosnevad numbritest, sealhulgas null. 
2. Muutujad koosnevad suurtest või väikestest ladina tähtedest, millele võib eelneda üks alakriips (`_`).
3. Aritmeetilised operatsioonid (`+`, `-`) koosnevad kahest avaldisest ja operaatorist nende vahel. Nad on vasakassotsiatiivsed ja sama prioriteediga.
4. Valikuoperaator koosneb kahest avaldisest, mis on eraldatud püstkriipsuga (`|`). Valikuoperaator on paremassotsiatiivne.
5. Omistamimine koosneb muutujast, võrdusmärgist (`=`) ja avaldisest.
6. Järjest väärtustamine koosneb kahest või enamast avaldisest, mis on eraldatud semikoolonitega (`;`). See tähendab, et ta on assotsiatiivne operaator: sisendi `a; b; c` korral tuleks tagastada `seq(a, b, c)`, mitte `seq(a, seq(b, c))` ega `seq(seq(a, b), c)`.
7. Operaatorite prioriteedid kahanevas järjekorras on: aritmeetilised operaatorid, valikuoperaator, omistamine ja järjest väärtustamine.
8. Avaldistes võib kasutada sulge, mis on kõige kõrgema prioriteediga.
9. Tühisümboleid (tühikud, tabulaatorid, reavahetused) tuleb ignoreerida.


## Lõviosa: OttCompiler

Klassis _OttCompiler_ tuleb implementeerida meetod _compile_, mis kompileerib Ott keele avaldise CMa programmiks. 
Kompileerimisele kehtivad järgmised nõuded:

1. Muutujate väärtused antakse stack'il etteantud järjekorras. 
2. Muutujatele järgnevad stack'il oraakli vastused. Me lubame ainult piiratud arv kordi oraakli käest küsida ja käivitame programmi selliselt, et stack'i peal on kõik tema tulevased vastused ette antud. Seda ei pea kontrollima, vaid võib eeldada, et üheski testis ei ületata seda piiri.
3. Võib eeldada, et muutujate seas on spetsiaalne nimega muutuja `oracle`, mille algne väärtus on stack'i indeks, millele järgnevad oraakli vastused (vt täpsustus allpool).
4. Programmi täitmise lõpuks peab stack'ile lisanduma täpselt üks element: avaldise väärtustamise tulemus, mis on sama nagu _OttEvaluator_-iga väärtustades. 
5. Programmi täitmisel võib üle kirjutada muutujate väärtusi, kaasaarvatud oraakli indeksile vastava muutuja `oracle`, aga oraakli enda väärtusi ei tohi üle kirjutada.
6. Võib eeldada, et defineerimata muutujaid ei kasutata.

> **PS.** Oraakli indeksi muutujat `oracle` soovitame kasutada selleks, et jälgida viimati loetud oraakli stack'i-pesa. Selle muutuja sisu on algselt ühe võrra väiksem kui pesa indeks, kus oraakli vastused algavad: oraakli poole pöördumisel tuleks selle muutuja väärtust suurendada ja seejärel lugeda oraakli vastus saadud indeksilt.
