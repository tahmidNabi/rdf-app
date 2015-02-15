package com.tnob;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Created by tahmid on 2/14/15.
 */
public abstract class RDFModelIterator {
    private Model model;

    protected RDFModelIterator(Model model) {
        this.model = model;
    }

    public final void iterateRDFModel() {
        StmtIterator iterator = model.listStatements();

        while (iterator.hasNext()) {
            Statement stmt = iterator.nextStatement();
            doInIterator(stmt);
        }

    }

    protected abstract void doInIterator(Statement stmt);
}
