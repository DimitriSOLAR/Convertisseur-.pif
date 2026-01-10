### VARIABLES ###

# Commande pour la compilation Java
JC = javac
# Options de compilation
JCFLAGS = -encoding UTF-8 -sourcepath $(SRCDIR)

# Commande pour l'exécution Java
JVM = java
JVMFLAGS = 

# Commande pour la création de JAR
JAR = jar

# Répertoires
SRCDIR = src
BUILDDIR = build

# CLASSPATH pour les classes de l'application
APP_CP = build

### RÈGLES PRINCIPALES ###

# Règle par défaut
all: compile

# Règle globale pour tout compiler
compile: $(BUILDDIR)/pif/Main.class

### RÈGLES POUR LES JARS ###

# Création du JAR pour le convertisseur
jar-convertisseur: compile
	$(JAR) cfe convertisseur.jar pif.Main -C $(BUILDDIR) .

# Création du JAR pour le visualisateur
jar-visualisateur: compile
	$(JAR) cfe visualisateur.jar pif.Main -C $(BUILDDIR) .

# Créer tous les JARs d'un coup
jars: jar-convertisseur jar-visualisateur

### RÈGLES DÉTAILLÉES PAR FICHIER ###

$(BUILDDIR)/pif/Main.class: $(SRCDIR)/pif/Main.java $(BUILDDIR)/pif/FenetreConvertisseur.class $(BUILDDIR)/pif/FenetreVisualisateur.class
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

$(BUILDDIR)/pif/FenetreConvertisseur.class: $(SRCDIR)/pif/FenetreConvertisseur.java $(BUILDDIR)/pif/ActionOuvrir.class $(BUILDDIR)/pif/ActionConvertir.class $(BUILDDIR)/pif/ImagePIF.class $(BUILDDIR)/pif/CodecHuffman.class
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

$(BUILDDIR)/pif/FenetreVisualisateur.class: $(SRCDIR)/pif/FenetreVisualisateur.java $(BUILDDIR)/pif/PanneauImage.class $(BUILDDIR)/pif/ImagePIF.class
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

$(BUILDDIR)/pif/ActionOuvrir.class: $(SRCDIR)/pif/ActionOuvrir.java
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

$(BUILDDIR)/pif/ActionConvertir.class: $(SRCDIR)/pif/ActionConvertir.java
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

$(BUILDDIR)/pif/PanneauImage.class: $(SRCDIR)/pif/PanneauImage.java $(BUILDDIR)/pif/EcouteurSouris.class
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

$(BUILDDIR)/pif/EcouteurSouris.class: $(SRCDIR)/pif/EcouteurSouris.java
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

$(BUILDDIR)/pif/ImagePIF.class: $(SRCDIR)/pif/ImagePIF.java $(BUILDDIR)/pif/CodecHuffman.class $(BUILDDIR)/pif/FluxEntreeBits.class $(BUILDDIR)/pif/FluxSortieBits.class
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

$(BUILDDIR)/pif/CodecHuffman.class: $(SRCDIR)/pif/CodecHuffman.java $(BUILDDIR)/pif/NoeudHuffman.class
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

$(BUILDDIR)/pif/NoeudHuffman.class: $(SRCDIR)/pif/NoeudHuffman.java
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

$(BUILDDIR)/pif/FluxEntreeBits.class: $(SRCDIR)/pif/FluxEntreeBits.java
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

$(BUILDDIR)/pif/FluxSortieBits.class: $(SRCDIR)/pif/FluxSortieBits.java
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<


### BUTS D'EXÉCUTION (VIA SOURCES COMPILÉES) ###

convertisseur: compile
	@echo "Lancement du convertisseur..."
	$(JVM) $(JVMFLAGS) -cp "$(APP_CP)" pif.Main convertisseur $(ARGS)

visualisateur: compile
	@echo "Lancement du visualisateur..."
	$(JVM) $(JVMFLAGS) -cp "$(APP_CP)" pif.Main visualisateur $(ARGS)

run:
	@echo "Usage: make [convertisseur|visualisateur] [ARGS=...]"

### RÈGLES DE NETTOYAGE ###

clean:
	@echo "Nettoyage des fichiers compilés et des JARs..."
	-rm -rf $(BUILDDIR)
	-rm -rf *.jar
mrproper: clean

### BUTS FACTICES ###

.PHONY: all compile convertisseur visualisateur run clean mrproper jar-convertisseur jar-visualisateur jars
