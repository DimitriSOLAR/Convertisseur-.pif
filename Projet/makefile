# --- VARIABLES ---
SRC_DIR := src
BUILD_DIR := build
PACKAGE := fr/univ/pif

# Compilateur et options
JC := javac
JVM := java
# -d : dossier de destination des .class
# -sourcepath : où trouver les sources
# -g : inclure les infos de debug
JFLAGS := -d $(BUILD_DIR) -sourcepath $(SRC_DIR) -g

# Trouve tous les fichiers .java dans le package
SOURCES := $(wildcard $(SRC_DIR)/$(PACKAGE)/*.java)
# Définit les fichiers .class correspondants dans le dossier bin
CLASSES := $(SOURCES:$(SRC_DIR)/%.java=$(BUILD_DIR)/%.class)

# Noms complets des classes principales (avec package)
MAIN_VISU := fr.univ.pif.Visualisateur
MAIN_CONV := fr.univ.pif.Convertisseur

# --- REGLES ---

# Règle par défaut : compile tout
default: $(CLASSES)

# Crée le dossier bin s'il n'existe pas
$(BUILD_DIR):
	mkdir -p $(BUILD_DIR)

# Règle générique de compilation : .java -> .class
$(BUILD_DIR)/%.class: $(SRC_DIR)/%.java | $(BUILD_DIR)
	$(JC) $(JFLAGS) $<

# --- EXECUTION ---

# Lance le Visualisateur (argument optionnel : ARGS="chemin/image.pif")
run-visu: default
	$(JVM) -cp $(BUILD_DIR) $(MAIN_VISU) $(ARGS)

# Lance le Convertisseur (argument optionnel : ARGS="chemin/image.jpg")
run-conv: default
	$(JVM) -cp $(BUILD_DIR) $(MAIN_CONV) $(ARGS)

# --- CREATION JAR (Demandé dans le sujet) ---

jars: visu.jar conv.jar

visu.jar: default
	jar cfe Visualisateur.jar $(MAIN_VISU) -C $(BUILD_DIR) .
	@echo "Archive Visualisateur.jar créée."

conv.jar: default
	jar cfe Convertisseur.jar $(MAIN_CONV) -C $(BUILD_DIR) .
	@echo "Archive Convertisseur.jar créée."

# --- NETTOYAGE ---

clean:
	rm -rf $(BUILD_DIR)
	rm -f *.jar
