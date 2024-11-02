# Myframework_photocopie
Photocopie est un framework java qui permet de copier toutes les propriétés d’une classe vers une autre lorsqu’elle existe. Par
exemple si l’on possède une instance de Person{firstName,lastName,phone} et de Company{firstName, address} copier Person
dans Company revient à copier le contenu de firstName uniquement. La signature de la méthode attendue est : copy(Object
from,Object to)
La classe Photocopie doit supporter les contraintes suivantes :
— Doit gérer les propriétés et non les attributs (“get/setX” et non “x”)
— Doit gérer l’héritage (et donc voir toutes les propriétés de toute la hérarchie)
— Doit supporter une conversion implicite entre String et int (lorsqu’une annotation que vous allez définir @Convert est
utilisée)

Ce framework est l'un des exemples demontrant une compétence en mécanisme de reflection et manipulation OOP
