const itemId = window.itemId;

auctionWS.on('connected', () => {
    console.log('Conectado! Suscribiéndose al item ' + itemId);
    auctionWS.subscribeToItem(itemId);
});

auctionWS.on('new_offer', (data) => {
    if (data.itemId === itemId) {
        console.log('💰 Nueva oferta recibida:', data);

        const highestEl = document.querySelector('.price-box.highlight .price-value');
        if (highestEl) {
            highestEl.textContent = '$' + data.amount;
            highestEl.style.animation = 'pulse 0.5s';
        }

        addOfferToList(data);

        showNotification(`Nueva oferta de ${data.userName}: $${data.amount}`);
    }
});

function addOfferToList(offerData) {
    const offersList = document.querySelector('.offers-list');
    const emptyState = offersList.querySelector('.empty-state');

    if (emptyState) {
        emptyState.remove();
    }

    const li = document.createElement('li');
    li.className = 'offer-item';
    li.innerHTML = `
            <div class="offer-content">
                <div class="offer-amount">$${offerData.amount}</div>
                <div class="offer-details">
                    <span class="offer-user">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                            <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                        ${offerData.userName}
                    </span>
                    <span class="offer-date">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="12" cy="12" r="10"></circle>
                            <polyline points="12 6 12 12 16 14"></polyline>
                        </svg>
                        Justo ahora
                    </span>
                </div>
            </div>
        `;

    offersList.insertBefore(li, offersList.firstChild);

}

function showNotification(message) {
    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.textContent = message;
    document.body.appendChild(notification);

    setTimeout(() => {
        notification.classList.add('show');
    }, 10);

    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}
