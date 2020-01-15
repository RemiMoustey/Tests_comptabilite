package com.dummy.myerp.consumer.dao.contrat;

import java.util.List;

import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.NotFoundException;


/**
 * Interface de DAO des objets du package Comptabilite
 */
public interface ComptabiliteDao {

    /**
     * Renvoie la liste des Comptes Comptables
     * @return {@link List}
     */
    List<CompteComptable> getListCompteComptable();


    /**
     * Renvoie la liste des Journaux Comptables
     * @return {@link List}
     */
    List<JournalComptable> getListJournalComptable();

    /**
     * Renvoie la liste des lignes d'écriture comptable
     * @return {@link List}
     */
    List<LigneEcritureComptable> getListLigneEcritureComptable();

    /**
     * Renvoie la liste des séquences d'écriture comptable
     * @return {@link List}
     */
    List<SequenceEcritureComptable> getListSequenceEcritureComptable();


    // ==================== EcritureComptable ====================

    /**
     * Renvoie la liste des Écritures Comptables
     * @return {@link List}
     */
    List<EcritureComptable> getListEcritureComptable();

    /**
     * Renvoie l'Écriture Comptable d'id {@code pId}.
     *
     * @param pId l'id de l'écriture comptable
     * @return {@link EcritureComptable}
     * @throws NotFoundException : Si l'écriture comptable n'est pas trouvée
     */
    EcritureComptable getEcritureComptable(Integer pId) throws NotFoundException;

    /**
     * Renvoie l'Écriture Comptable de référence {@code pRef}.
     *
     * @param pReference la référence de l'écriture comptable
     * @return {@link EcritureComptable}
     * @throws NotFoundException : Si l'écriture comptable n'est pas trouvée
     */
    EcritureComptable getEcritureComptableByRef(String pReference) throws NotFoundException;

    /**
     * Renvoie la ligne d'Écriture Comptable ayant pour ecriture_id {@code pEcritureId} et pour ligne_id {@code pLigneId}.
     *
     * @param pEcritureId l'id de l'écriture comptable
     * @param pLigneId l'id de la ligne d'écriture
     * @return {@link LigneEcritureComptable}
     * @throws NotFoundException : Si la ligne d'écriture comptable n'est pas trouvée
     */
    LigneEcritureComptable getLigneEcritureComptableByEcritureIdAndLigneId(int pEcritureId, int pLigneId) throws NotFoundException;

    /**
     * Charge la liste des lignes d'écriture de l'écriture comptable {@code pEcritureComptable}
     *
     * @param pEcritureComptable -
     */
    void loadListLigneEcriture(EcritureComptable pEcritureComptable);

    /**
     * Insert une nouvelle écriture comptable.
     *
     * @param pEcritureComptable -
     */
    void insertEcritureComptable(EcritureComptable pEcritureComptable);

    /**
     * Met à jour l'écriture comptable.
     *
     * @param pEcritureComptable -
     */
    void updateEcritureComptable(EcritureComptable pEcritureComptable);

    /**
     * Supprime l'écriture comptable d'id {@code pId}.
     *
     * @param pId l'id de l'écriture
     */
    void deleteEcritureComptable(Integer pId);

    boolean isJournalInDatabase(JournalComptable journalComptable);

    SequenceEcritureComptable getSequence(int annee, String code) throws NotFoundException;

    void insertSequenceEcritureComptable(int annee, String code);

    void updateSequenceEcritureComptable(int annee, String code, int derniereValeur);
}
