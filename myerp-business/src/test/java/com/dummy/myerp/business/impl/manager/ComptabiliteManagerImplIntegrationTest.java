package com.dummy.myerp.business.impl.manager;

import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ComptabiliteManagerImplIntegrationTest extends BusinessTestCase {

    ComptabiliteManager comptabiliteManager = getBusinessProxy().getComptabiliteManager();

    @Test
    public void selectEcrituresComptables() {
        Assert.assertEquals(5, comptabiliteManager.getListEcritureComptable().size());
    }

    @Test
    public void selectComptesComptables() {
        Assert.assertEquals(7, comptabiliteManager.getListCompteComptable().size());
    }

    @Test
    public void selectJournauxComptables() {
        Assert.assertEquals(4, comptabiliteManager.getListJournalComptable().size());
    }

    @Test
    public void tetIfAlreadyExistingReference() throws ParseException, FunctionalException, NotFoundException {
        EcritureComptable pEcritureComptable = new EcritureComptable();
        pEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Date date = new SimpleDateFormat("dd/M/yyyy").parse("31/12/2016");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        pEcritureComptable.setDate(calendar.getTime());
        pEcritureComptable.setLibelle("Achat");
        pEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(200),
                null));
        pEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null,
                new BigDecimal(200)));
        pEcritureComptable.setReference("AC-2016/00001");
        try {
            comptabiliteManager.insertEcritureComptable(pEcritureComptable);
        } catch(FunctionalException e) {
            assert (e.getMessage().contains("Une autre écriture comptable existe déjà avec la même référence."));
        }
    }


    @Test
    public void testInsertEcritureComptable() throws ParseException, FunctionalException, NotFoundException {
        EcritureComptable pEcritureComptable = new EcritureComptable();
        pEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Date date = new SimpleDateFormat("dd/M/yyyy").parse("31/12/2016");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        pEcritureComptable.setDate(calendar.getTime());
        pEcritureComptable.setLibelle("Achat");
        pEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(200),
                null));
        pEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null,
                new BigDecimal(200)));
        pEcritureComptable.getListLigneEcriture().get(0).getCompteComptable().setNumero(401);
        pEcritureComptable.getListLigneEcriture().get(1).getCompteComptable().setNumero(401);
        comptabiliteManager.addReference(pEcritureComptable);
        List<EcritureComptable> initialContent = comptabiliteManager.getListEcritureComptable();
        comptabiliteManager.insertEcritureComptable(pEcritureComptable);
        List<EcritureComptable> finalContent = comptabiliteManager.getListEcritureComptable();
        Assert.assertEquals(initialContent.size() + 1, finalContent.size());
    }

    @Test
    public void testUpdateEcritureComptable() throws FunctionalException {
        EcritureComptable pEcritureComptable = comptabiliteManager.getListEcritureComptable().get(2);
        pEcritureComptable.setLibelle("Nouveau libellé");
        comptabiliteManager.updateEcritureComptable(pEcritureComptable);
        for (EcritureComptable ecritureComptable : comptabiliteManager.getListEcritureComptable()) {
            if (ecritureComptable.getId() == pEcritureComptable.getId()) {
                Assert.assertEquals(pEcritureComptable.getLibelle(), ecritureComptable.getLibelle());
            }
        }
    }

    @Test
    public void testDeleteEcritureComptable() {
        List<EcritureComptable> initialContent = comptabiliteManager.getListEcritureComptable();
        comptabiliteManager.deleteEcritureComptable(-3);
        List<EcritureComptable> finalContent = comptabiliteManager.getListEcritureComptable();
        Assert.assertEquals(initialContent.size() - 1, finalContent.size());
    }
}