# Structural motif search in the PDB

This repository contains a script written in Java and based on the [BioJava](https://github.com/biojava/biojava) library to search for protein structures in the PDB that fulfill certain structural requirements.

## Installation

Clone the repository and install dependencies using maven.

## Usage

Motify the search criteria in the [MotifParams](https://github.com/lafita/motif-search/blob/master/src/main/java/main/MotifParams.java) class and run the [MotifSearch](https://github.com/lafita/motif-search/blob/master/src/main/java/main/MotifSearch.java) to start scanning PDB structures automatically.
The list of PDB IDs matching the search criteria will be printed to the standard output.
