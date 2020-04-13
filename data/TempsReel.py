import json
import requests
import os 

##A Changer le jena home si ca ne marche pas 
os.environ['JENA_HOME'] = 'C:\\Users\\HP\\Desktop\\A4\\S08\\dataMining\\Projet\\jena_Converter'
base = os.getcwd()
jena_bat = "..\jena_Converter\\bat"

def fromJsonTOrdf(ville, API):
    os.chdir(base)
    rempli = True 
    keyJson="records"
    while rempli:
        r = requests.get(API)
        resJson = r.json()
        if ville =="Lyon":
            keyJson="values"
        #print(len(resJson[keyJson]))
        if len(resJson[keyJson])>0:
            rempli = False 


    data_str = json.dumps(resJson)
    data_str = data_str[1:]

    with open('context_'+ville+'.txt') as f:
        conetxt = f.read()
    JsonLD = conetxt + data_str

    with open(ville+'.jsonld', 'w') as fp:
        fp.write(JsonLD)
    
    
    os.chdir(jena_bat)
    os.system("riot.bat --output=RDF/XML ..\\..\\data\\"+ville+".jsonld> ..\\..\\data\\"+ville+".rdf")

api_Paris='https://opendata.paris.fr/api/records/1.0/search/?dataset=velib-disponibilite-en-temps-reel&rows=500'
api_Lyon='https://download.data.grandlyon.com/ws/rdata/jcd_jcdecaux.jcdvelov/all.json?maxfeatures=500&start=1'
api_rennes='https://data.rennesmetropole.fr/api/records/1.0/search/?dataset=etat-des-stations-le-velo-star-en-temps-reel&rows=500'

fromJsonTOrdf("Paris",api_Paris)
fromJsonTOrdf("Lyon",api_Lyon)
fromJsonTOrdf("Rennes",api_rennes)

#api_Strasbourg='https://data.strasbourg.eu/api/records/1.0/search/?dataset=stations-velhop&rows=500'
#fromJsonTOrdf("Strasbourg",api_Strasbourg)

