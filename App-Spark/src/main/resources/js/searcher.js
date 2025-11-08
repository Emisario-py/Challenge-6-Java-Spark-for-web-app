document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    const itemsList = document.getElementById('List');
    const noResults = document.getElementById('noResults');
    const items = document.querySelectorAll('.item-element');
    const emptyState = document.querySelector('.empty-state');

    if (searchInput && items.length > 0) {
        searchInput.addEventListener('input', function(e) {
            const searchTerm = e.target.value.toLowerCase().trim();
            let visibleCount = 0;

            items.forEach(item => {
                const name = item.getAttribute('data-name').toLowerCase();
                const price = item.getAttribute('data-price');
                const highest = item.getAttribute('data-highest') || '';

                // Buscar en nombre y precios
                const matchesSearch = name.includes(searchTerm) ||
                    price.includes(searchTerm) ||
                    highest.includes(searchTerm);

                if (matchesSearch) {
                    item.style.display = '';
                    visibleCount++;
                } else {
                    item.style.display = 'none';
                }
            });

            if (visibleCount === 0 && searchTerm !== '') {
                noResults.style.display = 'block';
            } else {
                noResults.style.display = 'none';
            }
        });
    }
});