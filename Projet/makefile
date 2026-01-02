### VARIABLES ###

# Commande pour la compilation Java
JC = javac
# Options de compilation : UTF-8, cible Java 11, chemin source pour la résolution de dépendances
JCFLAGS = -encoding UTF-8 -Xlint:all --release 11 -sourcepath $(SRCDIR)

# Commande pour l'exécution Java
JVM = java
# Options de la JVM
JVMFLAGS = 

# Répertoire source
SRCDIR = src
# Chemin de compilation (Où les .class seront générés)
BUILDDIR = build
# CLASSPATH pour les classes de l'application
APP_CP = build

### RÈGLES PRINCIPALES ###

# Règle par défaut
all: compile

# Règle globale pour tout compiler (dépend de Main)
compile: $(BUILDDIR)/pif/Main.class

### RÈGLES DÉTAILLÉES PAR FICHIER ###

# Main.class
$(BUILDDIR)/pif/Main.class: $(SRCDIR)/pif/Main.java $(BUILDDIR)/pif/FenetreConvertisseur.class $(BUILDDIR)/pif/FenetreVisualisateur.class
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

# FenetreConvertisseur.class
$(BUILDDIR)/pif/FenetreConvertisseur.class: $(SRCDIR)/pif/FenetreConvertisseur.java $(BUILDDIR)/pif/ActionOuvrir.class $(BUILDDIR)/pif/ActionConvertir.class $(BUILDDIR)/pif/ImagePIF.class $(BUILDDIR)/pif/CodecHuffman.class
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

# FenetreVisualisateur.class
$(BUILDDIR)/pif/FenetreVisualisateur.class: $(SRCDIR)/pif/FenetreVisualisateur.java $(BUILDDIR)/pif/PanneauImage.class $(BUILDDIR)/pif/ImagePIF.class
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

# ActionOuvrir.class
$(BUILDDIR)/pif/ActionOuvrir.class: $(SRCDIR)/pif/ActionOuvrir.java
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

# ActionConvertir.class
$(BUILDDIR)/pif/ActionConvertir.class: $(SRCDIR)/pif/ActionConvertir.java
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

# PanneauImage.class
$(BUILDDIR)/pif/PanneauImage.class: $(SRCDIR)/pif/PanneauImage.java $(BUILDDIR)/pif/EcouteurSouris.class
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

# EcouteurSouris.class
$(BUILDDIR)/pif/EcouteurSouris.class: $(SRCDIR)/pif/EcouteurSouris.java
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

# ImagePIF.class
$(BUILDDIR)/pif/ImagePIF.class: $(SRCDIR)/pif/ImagePIF.java $(BUILDDIR)/pif/CodecHuffman.class $(BUILDDIR)/pif/FluxEntreeBits.class $(BUILDDIR)/pif/FluxSortieBits.class
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

# CodecHuffman.class
$(BUILDDIR)/pif/CodecHuffman.class: $(SRCDIR)/pif/CodecHuffman.java $(BUILDDIR)/pif/NoeudHuffman.class
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

# NoeudHuffman.class
$(BUILDDIR)/pif/NoeudHuffman.class: $(SRCDIR)/pif/NoeudHuffman.java
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

# FluxEntreeBits.class
$(BUILDDIR)/pif/FluxEntreeBits.class: $(SRCDIR)/pif/FluxEntreeBits.java
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<

# FluxSortieBits.class
$(BUILDDIR)/pif/FluxSortieBits.class: $(SRCDIR)/pif/FluxSortieBits.java
	@mkdir -p $(dir $@)
	$(JC) $(JCFLAGS) -cp "$(APP_CP)" -d $(BUILDDIR) $<


### BUTS D'EXÉCUTION ###

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
	@echo "Nettoyage des fichiers compilés..."
	-rm -rf $(BUILDDIR)

mrproper: clean

### BUTS FACTICES ###

.PHONY: all compile convertisseur visualisateur run clean mrproper
