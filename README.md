# Structural motif search in the PDB

This repository contains a script written in Java and based on the [BioJava](https://github.com/biojava/biojava) library to search for protein structures in the PDB that fulfill certain structural requirements.

The script was originally created to search for candidates for a fusion protein with two antiparallel terminal helices separated 11Å apart, in order to be able to solve its structure experimentally.

## Installation

Clone the repository and install dependencies using [Maven](https://maven.apache.org) with `mvn install`.

## Usage

Modify search criteria in the [MotifParams](https://github.com/lafita/motif-search/blob/master/src/main/java/main/MotifParams.java) class and run the [MotifSearch](https://github.com/lafita/motif-search/blob/master/src/main/java/main/MotifSearch.java) script to start scanning PDB structures automatically.
The list of PDB IDs matching the search criteria will be printed to the standard output.

## Citation

If you found this repository useful for your work, please consider citing the following publication:

**Cryo-EM structure of a single-chain β1–adrenoceptor – AmpC β-lactamase fusion protein** \
Gabriella Collu, Inayathulla Mohammed, Aleix Lafita, Tobias Bierig, Emiliya Poghosyan, Spencer Bliven, Roger M. Benoit \
*bioRxiv* (2021); doi: https://doi.org/10.1101/2021.09.25.461805

