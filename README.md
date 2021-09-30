# Structural motif search in the PDB

This repository contains a script written in Java and based on the
[BioJava](https://github.com/biojava/biojava) library to search for protein
structures in the PDB that fulfill certain structural requirements.

## Running

The latest release can be downloaded from
[github](https://github.com/lafita/motif-search/releases). Java 11 is
recommended. The search can be run using the `runMotifSearch.sh` script.

```
./runMotifSearch.sh
```

It will produce output about the search structures, concluding with a list of
all matching structures.

It may be desirable to modify the amount of memory available for the search.
This can be done by editing the `-Xmx` java flag in `runMotifSearch.sh`.

The script was originally created to search for candidates for a fusion protein
with two antiparallel terminal helices separated 11Å apart, in order to be able
to solve its structure experimentally.

## Installation

Clone the repository and install dependencies using
[Maven](https://maven.apache.org) with `mvn install`.

```
mvn package
```

## Usage

Modify search criteria in the
[MotifParams](https://github.com/lafita/motif-search/blob/master/src/main/java/main/MotifParams.java)
class and run the
[MotifSearch](https://github.com/lafita/motif-search/blob/master/src/main/java/main/MotifSearch.java)
script to start scanning PDB structures automatically. The list of PDB IDs
matching the search criteria will be printed to the standard output.

## Citation

If you found this repository useful for your work, please consider citing the
following publication:

**Cryo-EM structure of a single-chain β1–adrenoceptor – AmpC β-lactamase fusion
protein** \
Gabriella Collu, Inayathulla Mohammed, Aleix Lafita, Tobias Bierig, Emiliya
Poghosyan, Spencer Bliven, Roger M. Benoit \
*bioRxiv* (2021); doi: https://doi.org/10.1101/2021.09.25.461805
