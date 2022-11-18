package com.devsuperior.dsmeta.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import com.devsuperior.dsmeta.dto.SaleReportDTO;
import com.devsuperior.dsmeta.dto.SaleSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devsuperior.dsmeta.dto.SaleMinDTO;
import com.devsuperior.dsmeta.entities.Sale;
import com.devsuperior.dsmeta.repositories.SaleRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaleService {

	@Autowired
	private SaleRepository repository;
	@Transactional(readOnly = true)
	public SaleMinDTO findById(Long id) {
		Optional<Sale> result = repository.findById(id);
		Sale entity = result.get();
		return new SaleMinDTO(entity);
	}

	@Transactional(readOnly = true)
	public Page<SaleReportDTO> getReport(String dateMin, String dateMax, String sellerName, Pageable pageable) {
		LocalDate min = convertMinDate(dateMin);
		LocalDate max = convertMaxDate(dateMax);

		Page<Sale> result = repository.searchReport(min, max, sellerName, pageable);
		return result.map(x -> new SaleReportDTO(x));
	}

	@Transactional(readOnly = true)
	public List<SaleSummaryDTO> getSummary(String dateMin, String dateMax) {
		LocalDate min = convertMinDate(dateMin);
		LocalDate max = convertMaxDate(dateMax);

		List<Sale> result = repository.searchSummary(min, max);
		return summarySeller(result);
	}

	private LocalDate convertMaxDate(String dateMax) {
		LocalDate today = LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault());
		LocalDate max = ("".equals(dateMax)) ? today : LocalDate.parse(dateMax);
		return max;
	}

	private LocalDate convertMinDate(String dateMin) {
		LocalDate today = LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault());
		LocalDate overYear = today.minusYears(1L);
		LocalDate min = ("".equals(dateMin)) ? today : LocalDate.parse(dateMin);
		return min;
	}

	public List<SaleSummaryDTO> summarySeller(List<Sale> seller) {

		List<SaleSummaryDTO> list = new ArrayList<>();

		Map<String, Double> map = new TreeMap<>();
		for(Sale s1 : seller) {
			map.put(s1.getSeller().getName(), 0.0);
		}

		for (String s1 : map.keySet()) {
			double total = seller.stream()
					.filter(s -> s.getSeller().getName().equals(s1))
					.map(s -> s.getAmount()).reduce(0.0, (x, y) -> x + y);

			list.add(new SaleSummaryDTO(s1, total));
		}

		return list;
	}
}
