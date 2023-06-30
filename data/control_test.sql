select p.name as person_name, c.name as company_name from person p
left join company c
on p.company_id = c.id
where company_id <> 5 or company_id is null

select company_name, person_count from (
	select c.name as company_name, count(p.name) as person_count from company c join person p
	on c.id = p.company_id group by c.name
	) as sub1
	where person_count = (
		select max(person_count) from (
			select count(*) as person_count from person group by company_id
		) as sub2
	)