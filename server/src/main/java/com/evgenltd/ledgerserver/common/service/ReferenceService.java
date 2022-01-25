package com.evgenltd.ledgerserver.common.service;

import com.evgenltd.ledgerserver.common.entity.Reference;
import com.evgenltd.ledgerserver.common.repository.ReferenceRepository;
import com.evgenltd.ledgerserver.util.ApplicationException;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ReferenceService<T extends Reference, D, W> {

    protected final ReferenceRepository<T> referenceRepository;

    public ReferenceService(final ReferenceRepository<T> referenceRepository) {
        this.referenceRepository = referenceRepository;
    }

    public long count(final LoadConfig loadConfig) {
        return referenceRepository.count(loadConfig.toSpecification());
    }

    public List<W> list(final LoadConfig loadConfig) {
        return referenceRepository.findAll(
                        loadConfig.toSpecification(),
                        loadConfig.toPageRequest()
                ).stream()
                .map(this::toRow)
                .toList();
    }

    public List<ReferenceRecord> filter(final String filter) {
        return referenceRepository.findAll()
                .stream()
                .filter(transfer -> filter == null || filter.isBlank() || transfer.getName().contains(filter))
                .map(this::toReference)
                .collect(Collectors.toList());
    }

    public D byId(final Long id) {
        return referenceRepository.findById(id)
                .map(this::toRecord)
                .orElseThrow(() -> new ApplicationException("Transfer [%s] not found", id));
    }

    @Transactional
    public void update(final D record) {
        final T entity = toEntity(record);
        referenceRepository.save(entity);
    }

    @Transactional
    public void delete(final Long id) {
        referenceRepository.deleteById(id);
    }

    protected abstract W toRow(final T entity);

    private ReferenceRecord toReference(final T entity) {
        return new ReferenceRecord(entity.getId(), entity.getName());
    }

    protected abstract D toRecord(final T entity);

    protected abstract T toEntity(final D record);

}
