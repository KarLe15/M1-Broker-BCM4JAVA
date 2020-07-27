#!/bin/sh
##Simple algorithme shell permettant de generer des scenario aleatoire en json. Un argument correspondant au post fixe des fichiers test doit etre fournis en ligne de commande.

i="scen_mix_$1"
j=0
par_sus=$(awk -v min=0 -v max=2 'BEGIN{srand(); print int(min+rand()*(max-min+1))}')
par_pub=$(awk -v min=0 -v max=2 'BEGIN{srand(); print int(min+rand()*(max-min+1))}')
nb_pub=$(awk -v min=1 -v max=4 'BEGIN{srand(); print int(min+rand()*(max-min+1))}')
nb_sus=$(awk -v min=1 -v max=4 'BEGIN{srand(); print int(min+rand()*(max-min+1))}')
content=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
name=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
topic=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
topics="$topic"
topics="\"$topics\""
value=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
temps=$(awk -v min=0 -v max=3 'BEGIN{srand(); print int(min+rand()*(max-min+1))}')
repeated=$(awk -v min=10 -v max=500 'BEGIN{srand(); print int(min+rand()*(max-min+1))}')
tanttopic=$(awk -v min=0 -v max=4 'BEGIN{srand(); print int(min+rand()*(max-min+1))}')
tantfilter=$(awk -v min=0 -v max=4 'BEGIN{srand(); print int(min+rand()*(max-min+1))}')
filters="{\"name\" : \"$name\", \"value\" : \"$value\" }"
while ((j < tanttopic)); do
newtopic=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
topics="$topics ,\n \"$newtopic\""
j=$((j+1))
done
j=0
while ((j < tantfilter)); do
value=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
name=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
newfilters="{\"name\" : \"$name\", \"value\" : \"$value\" }"
filters="$filters ,\n $newfilters"
j=$((j+1))
done
ch_vr_pub="{
      \"content\" : \"$content\",
      \"filters\" : [
        $filters
      ],
      \"temps\"  : $temps,
      \"topics\" : [
        $topics
      ]
    }"

ch_vr_sus="{
      \"topic\" : \"$topic\",
      \"filters\" : [
        $filters
      ]
    }"

par_pub_ch=$ch_vr_pub
par_sus_ch=$ch_vr_sus
k=0
while ((k < par_pub)); do
contentp=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
namep=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
topicsp=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
topicsp="\"$topicsp\""
valuep=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
tempsp=$(awk -v min=0 -v max=4 'BEGIN{srand(); print int(min+rand()*(max-min+1))}')
repeatedp=$(awk -v min=10 -v max=500 'BEGIN{srand(); print int(min+rand()*(max-min+1))}')
tanttopicp=$(awk -v min=0 -v max=4 'BEGIN{srand(); print int(min+rand()*(max-min+1))}')
tantfilterp=$(awk -v min=0 -v max=4 'BEGIN{srand(); print int(min+rand()*(max-min+1))}')
filtersp="{\"name\" : \"$name\", \"value\" : \"$value\" }"
while ((j < tanttopicp)); do
newtopicp=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
topicsp="$topicsp ,\n \"$newtopicp\""
j=$((j+1))
done
j=0
while ((j < tantfilter)); do
valuep=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
namep=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
newfiltersp="{\"name\" : \"$namep\", \"value\" : \"$valuep\" }"
filtersp="$filtersp ,\n $newfiltersp"
j=$((j+1))
done
par_pub_ch="$par_pub_ch,\n{
      \"content\" : \"$contentp\",
      \"filters\" : [
        $filtersp
      ],
      \"temps\"  : $tempsp,
      \"topics\" : [
        $topicsp
      ]
    }"
k=$((k+1))
done
k=0
while ((k < par_sus)); do
contentp=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
namep=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
topicp=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
topicsp="$topicp"
topicsp="\"$topicsp\""
valuep=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
tempsp=$(awk -v min=0 -v max=4 'BEGIN{srand(); print int(min+rand()*(max-min+1))}')
repeatedp=$(awk -v min=10 -v max=500 'BEGIN{srand(); print int(min+rand()*(max-min+1))}')
tanttopicp=$(awk -v min=0 -v max=4 'BEGIN{srand(); print int(min+rand()*(max-min+1))}')
tantfilterp=$(awk -v min=0 -v max=4 'BEGIN{srand(); print int(min+rand()*(max-min+1))}')
filtersp="{\"name\" : \"$name\", \"value\" : \"$value\" }"
while ((j < tanttopicp)); do
newtopicp=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
topicsp="$topicsp ,\n \"$newtopicp\""
j=$((j+1))
done
j=0
while ((j < tantfilter)); do
valuep=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
namep=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
newfiltersp="{\"name\" : \"$namep\", \"value\" : \"$valuep\" }"
filtersp="$filtersp ,\n $newfiltersp"
j=$((j+1))
done
par_sus_ch="$par_sus_ch,\n{
      \"topic\" : \"$topicp\",
      \"filters\" : [
        $filtersp
      ]
    }"
k=$((k+1))
done

echo -e "{
  \"repeated\" : $repeated,
  \"messages\" :
  [
	$par_pub_ch
  ]\n
  }" > ./publishers/$i.json



echo -e "{
  \"subscriptions\": [
    $par_sus_ch
  ]
}" > ./subscribers/$i.json
ch_sus="CREATE,subscriber,$i,subscriber_1"
nb_mes=$((nb_pub*repeated))
ch_ex="\"subscriber_1\" : $nb_mes"
j=1
while ((j < nb_sus)); do
j=$((j+1))
ch_sus="$ch_sus\nCREATE,subscriber,$i,subscriber_$j"
ch_ex="$ch_ex,\n\"subscriber_$j\" : $nb_mes"
done
ch_pub="CREATE,publisher,$i,publisher_1"
j=1
while ((j < nb_pub)); do
j=$((j+1))
ch_pub="$ch_pub\nCREATE,publisher,$i,publisher_$j"
done
echo -e "## operation, component, scenario, uri\n$ch_sus\n$ch_pub" > $i.scenario
echo -e "{\n$ch_ex\n}" > $i.expected

echo "test registred at $i"



